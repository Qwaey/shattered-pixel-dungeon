# Static artwork/layout preview only. This does not launch the game.
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '../..')).Path
$javaRoot = Join-Path $repoRoot 'core/src/main/java/com/shatteredpixel/shatteredpixeldungeon'
$assetRoot = Join-Path $repoRoot 'core/src/main/assets/environment'
$floorCode = Get-Content -Raw (Join-Path $javaRoot 'tiles/SurfaceFloorTiles.java')
$pathCode = Get-Content -Raw (Join-Path $javaRoot 'tiles/SurfacePathTiles.java')
$forestCode = Get-Content -Raw (Join-Path $javaRoot 'tiles/SurfaceForest.java')
$levelCode = Get-Content -Raw (Join-Path $javaRoot 'levels/SurfaceLevel.java')

function String-Rows([string] $code, [string] $name) {
    $body = [regex]::Match($code, "(?s)\b$name\s*=\s*\{(.*?)\};").Groups[1].Value
    return @([regex]::Matches($body, '"([^"\r\n]*)"') | ForEach-Object { $_.Groups[1].Value })
}

$forestPlan = String-Rows $forestCode 'PLAN'
$fieldShape = String-Rows $floorCode 'FIELD_SHAPE'
$meadow = String-Rows $floorCode 'MEADOW'
$leafLitter = String-Rows $floorCode 'LEAF_LITTER'
$width = $forestPlan[0].Length * 2 + 2
$height = $forestPlan.Count * 2 + 2
$map = [int[,]]::new($width, $height)
$routesBody = [regex]::Match($pathCode, '(?s)ROUTES\s*=\s*\{(.*?)\};').Groups[1].Value
$routes = @([regex]::Matches($routesBody, '\{([^}]+)\}') | ForEach-Object { ,@([regex]::Matches($_.Groups[1].Value, '-?\d+') | ForEach-Object { [int]$_.Value }) })
$barnShape = String-Rows $pathCode 'BARN_PATH_SHAPE'
$barnPathX = [int][regex]::Match($pathCode, 'BARN_PATH_X\s*=\s*(-?\d+)').Groups[1].Value
$barnPathY = [int][regex]::Match($pathCode, 'BARN_PATH_Y\s*=\s*(-?\d+)').Groups[1].Value
$samples = [bool[,]]::new($width + 1, $height + 1)
for ($y = 0; $y -le $height; $y++) {
    for ($x = 0; $x -le $width; $x++) {
        $shapeX = $x - $barnPathX
        $shapeY = $y - $barnPathY
        if ($shapeX -ge 0 -and $shapeY -ge 0 -and $shapeY -lt $barnShape.Count -and $shapeX -lt $barnShape[0].Length) {
            $samples[$x, $y] = $barnShape[$shapeY][$shapeX] -eq '#'
        }
        foreach ($route in $routes) {
            for ($i = 0; $i -lt $route.Count - 2; $i += 2) {
                $startX = $route[$i] + 0.5
                $startY = $route[$i + 1] + 0.5
                $dx = $route[$i + 2] - $route[$i]
                $dy = $route[$i + 3] - $route[$i + 1]
                $along = [Math]::Clamp([double]((($x - $startX) * $dx + ($y - $startY) * $dy) / ($dx * $dx + $dy * $dy)), [double]0, [double]1)
                if ([Math]::Pow($x - $startX - $along * $dx, 2) + [Math]::Pow($y - $startY - $along * $dy, 2) -le 0.85 * 0.85) { $samples[$x, $y] = $true }
            }
        }
    }
}
$variants = @(@(0, 1, 0, 2), @(2, 0, 1, 0), @(1, 2, 0, 1))
$patches = @()
foreach ($match in [regex]::Matches($floorCode, '(?s)new Patch\((-?\d+),\s*(-?\d+),\s*(.*?)\)')) {
    $rows = if ($match.Groups[3].Value.Trim() -eq 'LEAF_LITTER') { $leafLitter } else { @([regex]::Matches($match.Groups[3].Value, '"([^"]*)"') | ForEach-Object { $_.Groups[1].Value }) }
    $patches += @{ X = [int]$match.Groups[1].Value; Y = [int]$match.Groups[2].Value; Rows = $rows }
}
function Ground-Tile([int] $x, [int] $y) {
    $fieldX = $x - [int]($width / 2) + 11
    $fieldY = $y - [int]($height / 2) - 4
    if ($fieldX -ge 0 -and $fieldY -ge 0 -and $fieldX -lt $fieldShape[0].Length - 1 -and $fieldY -lt $fieldShape.Count - 1) {
        $mask = [int]($fieldShape[$fieldY][$fieldX] -eq '#') + 2 * [int]($fieldShape[$fieldY][$fieldX + 1] -eq '#') + 4 * [int]($fieldShape[$fieldY + 1][$fieldX + 1] -eq '#') + 8 * [int]($fieldShape[$fieldY + 1][$fieldX] -eq '#')
        if ($mask -eq 15) { return 21 }
        if ($mask -ne 0) { return 256 + $mask }
    }
    $mask = [int]$samples[$x, $y] + 2 * [int]$samples[($x + 1), $y] + 4 * [int]$samples[($x + 1), ($y + 1)] + 8 * [int]$samples[$x, ($y + 1)]
    if ($mask -ne 0) { return 272 + 16 * $variants[$y % 3][$x % 4] + $mask }
    $detail = $meadow[$y % $meadow.Count][$x % $meadow[0].Length]
    foreach ($patch in $patches) {
        $patchX = $x - [int]($width / 2) - $patch.X
        $patchY = $y - [int]($height / 2) - $patch.Y
        if ($patchY -ge 0 -and $patchY -lt $patch.Rows.Count -and $patchX -ge 0 -and $patchX -lt $patch.Rows[$patchY].Length -and $patch.Rows[$patchY][$patchX] -ne ' ') { $detail = $patch.Rows[$patchY][$patchX] }
    }
    switch ($detail) { 'd' { return 6 }; 'p' { return 12 }; 'l' { return 13 }; 'f' { return 14 }; 's' { return 15 }; default { return 0 } }
}

$houses = @(@(8, 7, 8, 7, 12, 13), @(17, 16, 8, 6, 24, 19), @(29, 9, 8, 6, 32, 14), @(31, 21, 8, 6, 31, 24))
foreach ($house in $houses) {
    for ($y = $house[1]; $y -lt $house[1] + $house[3]; $y++) {
        for ($x = $house[0]; $x -lt $house[0] + $house[2]; $x++) {
            $map[$x, $y] = if ($x -eq $house[0] -or $y -eq $house[1] -or $x -eq $house[0] + $house[2] - 1 -or $y -eq $house[1] + $house[3] - 1) { 48 } else { 4 }
        }
    }
    $map[$house[4], $house[5]] = if ($house[0] -eq 29) { 58 } else { 56 }
}
$map[11, 10] = 17
$terrain = [System.Drawing.Bitmap]::new((Join-Path $assetRoot 'tiles_surface.png'))
$exterior = [System.Drawing.Bitmap]::new((Join-Path $assetRoot 'custom_tiles/surface_exterior.png'))
$canvas = [System.Drawing.Bitmap]::new($width * 16, $height * 16)
$graphics = [System.Drawing.Graphics]::FromImage($canvas)
for ($y = 0; $y -lt $height; $y++) {
    for ($x = 0; $x -lt $width; $x++) {
        $tile = if ($map[$x, $y] -ne 0) { $map[$x, $y] } else { Ground-Tile $x $y }
        $sourceRect = [System.Drawing.Rectangle]::new(($tile % 16) * 16, [int][Math]::Floor($tile / 16) * 16, 16, 16)
        $graphics.DrawImage($terrain, [System.Drawing.Rectangle]::new($x * 16, $y * 16, 16, 16), $sourceRect, [System.Drawing.GraphicsUnit]::Pixel)
    }
}
$objects = @()
$treeColumns = @(0, 16, 18, 20, 22, 4, 24, 26, 28, 30)
for ($row = 0; $row -lt $forestPlan.Count; $row++) {
    for ($col = 0; $col -lt $forestPlan[$row].Length; $col++) {
        $symbol = $forestPlan[$row][$col]
        if ($symbol -eq '.') { continue }
        $tree = if ($symbol -ge 'a') { 20 + [int]$symbol - [int][char]'a' } elseif ($symbol -ge 'A') { 10 + [int]$symbol - [int][char]'A' } else { [int]$symbol - [int][char]'0' }
        $spriteWidth = if ($tree -ge 20) { 1 } else { 2 }
        $spriteHeight = if ($tree -ge 20) { 2 } else { 3 }
        $spriteColumn = if ($tree -ge 20) { 32 + $tree - 20 } else { $treeColumns[$tree % 10] }
        $baseX = 1 + $col * 2 + $row % 2 + $(if ($tree -ge 20) { ($tree - 20) % 2 } else { 0 })
        $baseY = 1 + $row * 2 + $(if ($tree -eq 21 -or $tree -eq 22) { 0 } else { 1 })
        $objects += @{ X = $baseX; Y = $baseY; Column = $spriteColumn; W = $spriteWidth; H = $spriteHeight }
    }
}
$types = @{ OAK = @(0, 2, 3); PINE = @(4, 2, 3); CHOPPING_BLOCK = @(14, 1, 1) }
foreach ($match in [regex]::Matches($levelCode, 'placeExterior\((\d+),\s*(\d+),\s*SurfaceExterior.Type.(\w+)\)')) {
    $type = $types[$match.Groups[3].Value]
    if ($null -ne $type) { $objects += @{ X = [int]$match.Groups[1].Value; Y = [int]$match.Groups[2].Value; Column = $type[0]; W = $type[1]; H = $type[2] } }
}
foreach ($object in @(@(27, 19, 8, 2, 2), @(46, 20, 10, 2, 1), @(19, 27, 12, 2, 2), @(24, 26, 12, 2, 2), @(24, 30, 12, 2, 2))) {
    $objects += @{ X = $object[0]; Y = $object[1]; Column = $object[2]; W = $object[3]; H = $object[4] }
}
foreach ($object in ($objects | Sort-Object Y, X)) {
    $sourceRect = [System.Drawing.Rectangle]::new($object.Column * 16, (3 - $object.H) * 16, $object.W * 16, $object.H * 16)
    $destination = [System.Drawing.Rectangle]::new($object.X * 16, ($object.Y - $object.H + 1) * 16, $object.W * 16, $object.H * 16)
    $graphics.DrawImage($exterior, $destination, $sourceRect, [System.Drawing.GraphicsUnit]::Pixel)
}
$graphics.Dispose()
$canvas.Save((Join-Path $PSScriptRoot 'surface-paths-layout.png'), [System.Drawing.Imaging.ImageFormat]::Png)
$crop = $canvas.Clone([System.Drawing.Rectangle]::new(8 * 16, 6 * 16, 33 * 16, 26 * 16), [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$preview = [System.Drawing.Bitmap]::new($crop.Width * 2, $crop.Height * 2)
$graphics = [System.Drawing.Graphics]::FromImage($preview)
$graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
$graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
$graphics.DrawImage($crop, 0, 0, $preview.Width, $preview.Height)
$graphics.Dispose()
$preview.Save((Join-Path $PSScriptRoot 'surface-paths-village-preview.png'), [System.Drawing.Imaging.ImageFormat]::Png)
foreach ($bitmap in @($terrain, $exterior, $canvas, $crop, $preview)) { $bitmap.Dispose() }
Write-Output 'Rendered static village artwork and route preview; interiors and actors are omitted.'
