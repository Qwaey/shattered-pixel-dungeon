# Packs image_gen materials into a separate surface sheet; never edits the wooden source.
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '../..')).Path
$assetRoot = Join-Path $repoRoot 'core/src/main/assets/environment'
$generatedRoot = Join-Path $PSScriptRoot 'generated/earth-cliffs'
$palette = @('#30291e', '#3e3023', '#503b29', '#65482f', '#7b5b3b', '#92734d', '#26341e', '#354426', '#475634', '#5a653c', '#6a7045', '#89806a') | ForEach-Object { [System.Drawing.ColorTranslator]::FromHtml($_) }
function Nearest-Color($color) {
    $best = $palette[0]
    $distance = [double]::PositiveInfinity
    foreach ($candidate in $palette) {
        $d = [Math]::Pow($color.R - $candidate.R, 2) + [Math]::Pow($color.G - $candidate.G, 2) + [Math]::Pow($color.B - $candidate.B, 2)
        if ($d -lt $distance) { $distance = $d; $best = $candidate }
    }
    return $best
}
function Native-Tile([string] $name) {
    $source = [System.Drawing.Bitmap]::new((Join-Path $generatedRoot "$name.png"))
    $tile = [System.Drawing.Bitmap]::new(16, 16)
    for ($y = 0; $y -lt 16; $y++) {
        for ($x = 0; $x -lt 16; $x++) {
            $color = $source.GetPixel([int][Math]::Floor(($x + 0.5) * $source.Width / 16), [int][Math]::Floor(($y + 0.5) * $source.Height / 16))
            $tile.SetPixel($x, $y, (Nearest-Color $color))
        }
    }
    $source.Dispose()
    return $tile
}
$face = Native-Tile 'face'
$cap = Native-Tile 'cap'
$face.Save((Join-Path $assetRoot 'surface_earth_wall_face.png'), [System.Drawing.Imaging.ImageFormat]::Png)
$cap.Save((Join-Path $assetRoot 'surface_earth_wall_cap.png'), [System.Drawing.Imaging.ImageFormat]::Png)
$source = [System.Drawing.Bitmap]::new((Join-Path $assetRoot 'tiles_surface.png'))
if ($source.Width -ne 256 -or $source.Height -ne 320) { throw 'Expected the existing 256x320 surface sheet.' }
$sheet = [System.Drawing.Bitmap]::new(256, 576)
for ($y = 0; $y -lt 320; $y++) {
    for ($x = 0; $x -lt 256; $x++) { $sheet.SetPixel($x, $y, $source.GetPixel($x, $y)) }
}
# Appended slots 320-575 use the same local tile numbers as the original sheet.
for ($y = 0; $y -lt 256; $y++) {
    for ($x = 0; $x -lt 256; $x++) { $sheet.SetPixel($x, 320 + $y, $source.GetPixel($x, $y)) }
}
$slots = @(48, 52) + @(80..83) + @(88..91) + @(96..99) + @(144..159) + @(192..195)
foreach ($slot in $slots) {
    $left = ($slot % 16) * 16
    $top = [int][Math]::Floor($slot / 16) * 16
    $isCap = ($slot -ge 144 -and $slot -le 159) -or ($slot -ge 192 -and $slot -le 195)
    $material = if ($isCap) { $cap } else { $face }
    for ($y = 0; $y -lt 16; $y++) {
        for ($x = 0; $x -lt 16; $x++) {
            $mask = $source.GetPixel($left + $x, $top + $y)
            # Retain exact silhouettes, transparent pixels, and black internal voids.
            if ($mask.A -eq 0 -or ($mask.R + $mask.G + $mask.B) -lt 12) { continue }
            $materialX = if ($slot -eq 52 -or ($slot -ge 96 -and $slot -le 99)) { ($x + 5) % 16 } else { $x }
            $color = $material.GetPixel($materialX, $y)
            $shade = 1.0
            if (-not $isCap -and $slot -ge 80 -and $slot -le 99) {
                if ((($slot % 4) -band 1) -ne 0 -and $x -ge 14) { $shade = 0.65 }
                if ((($slot % 4) -band 2) -ne 0 -and $x -lt 2) { $shade = 0.65 }
            }
            $sheet.SetPixel($left + $x, 320 + $top + $y, [System.Drawing.Color]::FromArgb($mask.A, [int]($color.R * $shade), [int]($color.G * $shade), [int]($color.B * $shade)))
        }
    }
}
$sheet.Save((Join-Path $assetRoot 'tiles_surface_cliffs.png'), [System.Drawing.Imaging.ImageFormat]::Png)
# Artwork QA only: original wooden and new earthen versions in parallel rows.
$preview = [System.Drawing.Bitmap]::new(512, 192)
$graphics = [System.Drawing.Graphics]::FromImage($preview)
$graphics.Clear([System.Drawing.Color]::FromArgb(35, 35, 30))
$graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
$graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
$showSlots = @(48, 80, 81, 82, 83, 145, 150, 195)
for ($i = 0; $i -lt $showSlots.Count; $i++) {
    $slot = $showSlots[$i]
    $rect = [System.Drawing.Rectangle]::new(($slot % 16) * 16, [int][Math]::Floor($slot / 16) * 16, 16, 16)
    $graphics.DrawImage($sheet, [System.Drawing.Rectangle]::new($i * 64, 0, 64, 64), $rect, [System.Drawing.GraphicsUnit]::Pixel)
    $rect.Y += 320
    $graphics.DrawImage($sheet, [System.Drawing.Rectangle]::new($i * 64, 64, 64, 64), $rect, [System.Drawing.GraphicsUnit]::Pixel)
}
$graphics.DrawImage($face, [System.Drawing.Rectangle]::new(0, 128, 64, 64))
$graphics.DrawImage($cap, [System.Drawing.Rectangle]::new(64, 128, 64, 64))
$graphics.Dispose()
$preview.Save((Join-Path $PSScriptRoot 'earth-cliffs-tiles.png'), [System.Drawing.Imaging.ImageFormat]::Png)
foreach ($bitmap in @($source, $face, $cap, $sheet, $preview)) { $bitmap.Dispose() }
Write-Output 'Packed 34 earth-wall variants; the original 256x320 surface sheet is unchanged.'
