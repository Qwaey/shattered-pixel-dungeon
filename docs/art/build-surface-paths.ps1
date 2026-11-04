# Packs the image_gen artwork into native tiles; run from any directory.
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '../..')).Path
$environmentRoot = Join-Path $repoRoot 'core/src/main/assets/environment'
$generatedRoot = Join-Path $PSScriptRoot 'generated/stump-paths'

function Read-Bitmap([string] $path) {
    return [System.Drawing.Bitmap]::new($path)
}

function Save-Bitmap($bitmap, [string] $path) {
    $bitmap.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
}

function Nearest-Color($color, $palette) {
    $best = $palette[0]
    $bestDistance = [int]::MaxValue
    foreach ($candidate in $palette) {
        $distance = [Math]::Pow($color.R - $candidate.R, 2) + [Math]::Pow($color.G - $candidate.G, 2) + [Math]::Pow($color.B - $candidate.B, 2)
        if ($distance -lt $bestDistance) { $best = $candidate; $bestDistance = $distance }
    }
    return $best
}

function Native-Tile([string] $name, $palette, [bool] $transparent = $false) {
    $source = Read-Bitmap (Join-Path $generatedRoot "$name.png")
    $tile = [System.Drawing.Bitmap]::new(16, 16)
    for ($y = 0; $y -lt 16; $y++) {
        for ($x = 0; $x -lt 16; $x++) {
            $color = $source.GetPixel([int][Math]::Floor(($x + 0.5) * $source.Width / 16), [int][Math]::Floor(($y + 0.5) * $source.Height / 16))
            if ($transparent -and $color.A -lt 128) { $color = [System.Drawing.Color]::FromArgb(0, 0, 0, 0) }
            else { $color = Nearest-Color $color $palette }
            $tile.SetPixel($x, $y, $color)
        }
    }
    $source.Dispose()
    return $tile
}

function Rotated($bitmap, [int] $turns) {
    $copy = [System.Drawing.Bitmap]::new($bitmap)
    for ($i = 0; $i -lt $turns; $i++) { $copy.RotateFlip([System.Drawing.RotateFlipType]::Rotate90FlipNone) }
    return $copy
}

function Copy-Tile($target, $source, [int] $left, [int] $top) {
    for ($y = 0; $y -lt $source.Height; $y++) {
        for ($x = 0; $x -lt $source.Width; $x++) { $target.SetPixel($left + $x, $top + $y, $source.GetPixel($x, $y)) }
    }
}

function Enlarged($bitmap, [int] $scale) {
    $preview = [System.Drawing.Bitmap]::new($bitmap.Width * $scale, $bitmap.Height * $scale)
    $graphics = [System.Drawing.Graphics]::FromImage($preview)
    $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
    $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
    $graphics.DrawImage($bitmap, 0, 0, $preview.Width, $preview.Height)
    $graphics.Dispose()
    return $preview
}

$grass = Read-Bitmap (Join-Path $environmentRoot 'surface_grass.png')
$grassPalette = @{}
for ($y = 0; $y -lt 16; $y++) {
    for ($x = 0; $x -lt 16; $x++) { $color = $grass.GetPixel($x, $y); $grassPalette[$color.ToArgb()] = $color }
}
$mudPalette = @('#514735', '#66543b', '#716044', '#7b694b') | ForEach-Object { [System.Drawing.ColorTranslator]::FromHtml($_) }
$palette = @($grassPalette.Values) + $mudPalette
$mudColors = @{}
foreach ($color in $mudPalette) { $mudColors[$color.ToArgb()] = $true }
$baseTiles = @()
foreach ($letter in @('a', 'b', 'c')) {
    $tile = Native-Tile "path_base_$letter" $palette
    $baseTiles += $tile
    Save-Bitmap $tile (Join-Path $environmentRoot "surface_path_$letter.png")
}
$sources = @{}
foreach ($name in @('edge', 'corner', 'inset')) {
    $sources[$name] = Native-Tile "path_$name" $palette
    Save-Bitmap $sources[$name] (Join-Path $environmentRoot "surface_path_$name.png")
}

# Clockwise corner bits: NW=1, NE=2, SE=4, SW=8.
$shapes = @{}
foreach ($pair in @(@('corner', 4), @('edge', 12), @('inset', 14))) {
    $mask = [int]$pair[1]
    for ($turn = 0; $turn -lt 4; $turn++) {
        $shapes[$mask] = Rotated $sources[$pair[0]] $turn
        $mask = (($mask -shl 1) -band 15) -bor (($mask -shr 3) -band 1)
    }
}
foreach ($mask in @(5, 10)) {
    $shape = [System.Drawing.Bitmap]::new(16, 16)
    for ($y = 0; $y -lt 16; $y++) {
        for ($x = 0; $x -lt 16; $x++) {
            $corner = if ($mask -eq 5) { if ($x + $y -lt 16) { 1 } else { 4 } } else { if ($x -ge $y) { 2 } else { 8 } }
            $shape.SetPixel($x, $y, $shapes[$corner].GetPixel($x, $y))
        }
    }
    $shapes[$mask] = $shape
}

$transitions = [System.Drawing.Bitmap]::new(256, 48)
for ($variant = 0; $variant -lt 3; $variant++) {
    for ($mask = 0; $mask -lt 16; $mask++) {
        for ($y = 0; $y -lt 16; $y++) {
            for ($x = 0; $x -lt 16; $x++) {
                $color = if ($mask -eq 0) { $grass.GetPixel($x, $y) } elseif ($mask -eq 15) { $baseTiles[$variant].GetPixel($x, $y) } else { $shapes[$mask].GetPixel($x, $y) }
                $isMud = $mudColors.ContainsKey($color.ToArgb())
                if (-not $isMud) { $color = $grass.GetPixel($x, $y) }
                elseif ($variant -gt 0 -and $x -gt 1 -and $x -lt 14 -and $y -gt 1 -and $y -lt 14) { $color = $baseTiles[$variant].GetPixel($x, $y) }

                # All variants share canonical border samples, so their edges meet.
                $edgeBit = 0
                if ($x -eq 0) { $edgeBit = if ($y -lt 8) { 1 } else { 8 } }
                elseif ($x -eq 15) { $edgeBit = if ($y -lt 8) { 2 } else { 4 } }
                elseif ($y -eq 0) { $edgeBit = if ($x -lt 8) { 1 } else { 2 } }
                elseif ($y -eq 15) { $edgeBit = if ($x -lt 8) { 8 } else { 4 } }
                if ($edgeBit -ne 0) {
                    $color = if (($mask -band $edgeBit) -ne 0) { Nearest-Color ($baseTiles[0].GetPixel($x, $y)) $mudPalette } else { $grass.GetPixel($x, $y) }
                }
                $transitions.SetPixel($mask * 16 + $x, $variant * 16 + $y, $color)
            }
        }
    }
}
Save-Bitmap $transitions (Join-Path $environmentRoot 'surface_path_tiles.png')
$terrainPath = Join-Path $environmentRoot 'tiles_surface.png'
$oldTerrain = Read-Bitmap $terrainPath
$terrain = [System.Drawing.Bitmap]::new(256, 320)
for ($y = 0; $y -lt 272; $y++) {
    for ($x = 0; $x -lt 256; $x++) { $terrain.SetPixel($x, $y, $oldTerrain.GetPixel($x, $y)) }
}
$oldTerrain.Dispose()
Copy-Tile $terrain $transitions 0 272
Save-Bitmap $terrain $terrainPath

$block = Read-Bitmap (Join-Path $environmentRoot 'exterior/chopping_block.png')
$woodPalette = @{}
for ($y = 0; $y -lt 16; $y++) {
    for ($x = 0; $x -lt 16; $x++) {
        $color = $block.GetPixel($x, $y)
        if ($color.A -ge 128 -and (($color.R -gt $color.G * 1.12 -and $color.R -gt $color.B * 1.12) -or $color.R -lt 55)) {
            $opaqueColor = [System.Drawing.Color]::FromArgb(255, $color.R, $color.G, $color.B)
            $woodPalette[$opaqueColor.ToArgb()] = $opaqueColor
        }
    }
}
$stump = Native-Tile 'stump' @($woodPalette.Values) $true
Save-Bitmap $stump (Join-Path $environmentRoot 'exterior/stump.png')
$exteriorPath = Join-Path $environmentRoot 'custom_tiles/surface_exterior.png'
$oldExterior = Read-Bitmap $exteriorPath
$exterior = [System.Drawing.Bitmap]::new($oldExterior.Width, $oldExterior.Height)
Copy-Tile $exterior $oldExterior 0 0
$oldExterior.Dispose()
Copy-Tile $exterior $stump 224 32
Save-Bitmap $exterior $exteriorPath

$comparison = [System.Drawing.Bitmap]::new(32, 16)
Copy-Tile $comparison $grass 0 0
Copy-Tile $comparison $grass 16 0
$graphics = [System.Drawing.Graphics]::FromImage($comparison)
$graphics.DrawImageUnscaled($block, 0, 0)
$graphics.DrawImageUnscaled($stump, 16, 0)
$graphics.Dispose()
$stumpPreview = Enlarged $comparison 12
Save-Bitmap $stumpPreview (Join-Path $PSScriptRoot 'surface-stump-preview.png')
$pathPreview = Enlarged $transitions 4
Save-Bitmap $pathPreview (Join-Path $PSScriptRoot 'surface-path-tiles-preview.png')

foreach ($bitmap in @($grass, $block, $stump, $exterior, $terrain, $comparison, $stumpPreview, $pathPreview, $transitions) + $baseTiles + @($sources.Values) + @($shapes.Values)) { $bitmap.Dispose() }
Write-Output 'Packed stump, three path materials, 48 transition tiles, and atlas previews.'
