# Static artwork preview from existing game assets; does not launch the game.
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
. (Join-Path $PSScriptRoot 'ground-preview-utils.ps1')
$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '../..')).Path
$javaRoot = Join-Path $repoRoot 'core/src/main/java/com/shatteredpixel/shatteredpixeldungeon'
$assetRoot = Join-Path $repoRoot 'core/src/main/assets/environment'
$levelCode = Get-Content -Raw (Join-Path $javaRoot 'levels/WoodlandLevel.java')
$floorCode = Get-Content -Raw (Join-Path $javaRoot 'tiles/WoodlandFloorTiles.java')
$pathCode = Get-Content -Raw (Join-Path $javaRoot 'tiles/SurfacePathTiles.java')
$fadeCode = Get-Content -Raw (Join-Path $javaRoot 'effects/PassageFade.java')
$wallMargin = [int][regex]::Match($fadeCode, 'WALL_MARGIN\s*=\s*(\d+)').Groups[1].Value

function String-Rows([string] $code, [string] $name) {
    $body = [regex]::Match($code, "(?s)\b$name\s*=\s*\{(.*?)\};").Groups[1].Value
    return @([regex]::Matches($body, '"([^"\r\n]*)"') | ForEach-Object { $_.Groups[1].Value })
}
$forest = String-Rows $levelCode 'FOREST'
$meadow = String-Rows $floorCode 'MEADOW'
$width = $forest[0].Length * 2 + 2
$height = $forest.Count * 2 + 2
$routeBody = [regex]::Match($floorCode, '(?s)ROUTES\s*=\s*\{(.*?)\};').Groups[1].Value
$routes = @([regex]::Matches($routeBody, '\{([^}]+)\}') | ForEach-Object { ,@([regex]::Matches($_.Groups[1].Value, '\d+') | ForEach-Object { [int]$_.Value }) })
$trailWear = String-Rows $pathCode 'TRAIL_WEAR'
$samples = Route-Samples $width $height $routes $trailWear
$variants = @(@(0, 1, 0, 2), @(2, 0, 1, 0), @(1, 2, 0, 1))
function Ground-Tile([int] $x, [int] $y) {
    $mask = [int]$samples[$x, $y] + 2 * [int]$samples[($x + 1), $y] + 4 * [int]$samples[($x + 1), ($y + 1)] + 8 * [int]$samples[$x, ($y + 1)]
    if ($mask -ne 0) { return 272 + 16 * $variants[$y % 3][$x % 4] + $mask }
    $y -= 4
    if ($y -ge 18 -and $meadow[($y - 18) % $meadow.Count][$x % $meadow[0].Length] -eq 'f') { return 14 }
    if ($y -lt 14 -and ($x + 2 * $y) % 7 -eq 0) { return 13 }
    if ($y -ge 12 -and (2 * $x + $y) % 19 -eq 0) { return 14 }
    if (($x + 3 * $y) % 23 -eq 0) { return 12 }
    return 0
}

$terrain = [System.Drawing.Bitmap]::new((Join-Path $assetRoot 'tiles_surface_cliffs.png'))
$exterior = [System.Drawing.Bitmap]::new((Join-Path $assetRoot 'custom_tiles/surface_exterior.png'))
$furniture = [System.Drawing.Bitmap]::new((Join-Path $assetRoot 'custom_tiles/surface_furniture.png'))
$axe = [System.Drawing.Bitmap]::new((Join-Path $assetRoot 'exterior/chopping_block.png'))
$canvas = [System.Drawing.Bitmap]::new($width * 16, $height * 16)
$graphics = [System.Drawing.Graphics]::FromImage($canvas)
function Draw-Sprite($bitmap, [int] $sourceX, [int] $sourceY, [int] $w, [int] $h, [int] $x, [int] $y) {
    $graphics.DrawImage($bitmap, [System.Drawing.Rectangle]::new($x * 16, $y * 16, $w * 16, $h * 16), [System.Drawing.Rectangle]::new($sourceX, $sourceY, $w * 16, $h * 16), [System.Drawing.GraphicsUnit]::Pixel)
}
$houseX = [int][regex]::Match($levelCode, 'HOUSE_X\s*=\s*(\d+)').Groups[1].Value
$houseY = [int][regex]::Match($levelCode, 'HOUSE_Y\s*=\s*(\d+)').Groups[1].Value
for ($y = 0; $y -lt $height; $y++) {
    for ($x = 0; $x -lt $width; $x++) {
        $tile = Ground-Tile $x $y
        if ($x -eq 0 -or $y -eq 0 -or $x -eq $width - 1 -or $y -eq $height - 1) { $tile = 320 + 48 }
        if ($y -eq 0 -and $x -ge 10 - $wallMargin -and $x -le 11 + $wallMargin) { $tile = 320 + 144 }
        if ($x -ge $houseX -and $x -le $houseX + 7 -and $y -ge $houseY -and $y -le $houseY + 6) {
            $tile = if ($x -eq $houseX -or $x -eq $houseX + 7 -or $y -eq $houseY -or $y -eq $houseY + 6) { 48 } else { 4 }
            if ($x -eq $houseX + 3 -and $y -eq $houseY + 6) { $tile = 56 }
        }
        Draw-Sprite $terrain (($tile % 16) * 16) ([int][Math]::Floor($tile / 16) * 16) 1 1 $x $y
    }
}
$objects = @()
$treeColumns = @(0, 16, 18, 20, 22, 4, 24, 26, 28, 30)
for ($row = 0; $row -lt $forest.Count; $row++) {
    for ($col = 0; $col -lt $forest[$row].Length; $col++) {
        $symbol = $forest[$row][$col]
        if ($symbol -eq '.') { continue }
        $tree = if ($symbol -ge 'a') { 20 + [int]$symbol - [int][char]'a' } elseif ($symbol -ge 'A') { 10 + [int]$symbol - [int][char]'A' } else { [int]$symbol - [int][char]'0' }
        $w = if ($tree -ge 20) { 1 } else { 2 }
        $h = if ($tree -ge 20) { 2 } else { 3 }
        $column = if ($tree -ge 20) { 32 + $tree - 20 } else { $treeColumns[$tree % 10] }
        $baseX = 1 + $col * 2 + $row % 2 + $(if ($tree -ge 20) { ($tree - 20) % 2 } else { 0 })
        $baseY = 1 + $row * 2 + $(if ($tree -eq 21 -or $tree -eq 22) { 0 } else { 1 })
        $objects += @{ X = $baseX; Y = $baseY; Column = $column; W = $w; H = $h }
    }
}
$types = @{ OAK = @(0, 2, 3); PINE = @(4, 2, 3); CHOPPING_BLOCK = @(14, 1, 1) }
foreach ($match in [regex]::Matches($levelCode, 'placeExterior\((\d+),\s*(\d+),\s*SurfaceExterior.Type.(\w+)\)')) {
    $type = $types[$match.Groups[3].Value]
    $objects += @{ X = [int]$match.Groups[1].Value; Y = [int]$match.Groups[2].Value; Column = $type[0]; W = $type[1]; H = $type[2] }
}
foreach ($object in ($objects | Sort-Object Y, X)) {
    Draw-Sprite $exterior ($object.Column * 16) ((3 - $object.H) * 16) $object.W $object.H $object.X ($object.Y - $object.H + 1)
}
$furnitureColumns = @{ BED = 0; TABLE = 1; STOOL = 2; CUPBOARD = 3; STOVE = 4; FIREWOOD = 5; WASHBASIN = 6 }
foreach ($match in [regex]::Matches($levelCode, 'placeFurniture\(HOUSE_X \+ (\d+), HOUSE_Y \+ (\d+), Type.(\w+)\)')) {
    $type = $match.Groups[3].Value
    $h = if ($type -eq 'BED') { 2 } else { 1 }
    Draw-Sprite $furniture ($furnitureColumns[$type] * 16) $(if ($h -eq 2) { 0 } else { 16 }) 1 $h ($houseX + [int]$match.Groups[1].Value) ($houseY + [int]$match.Groups[2].Value)
}
foreach ($match in [regex]::Matches($levelCode, 'placeWoodpile\((\d+),\s*(\d+)\)')) {
    Draw-Sprite $furniture (5 * 16) 16 1 1 ([int]$match.Groups[1].Value) ([int]$match.Groups[2].Value)
}
$axeMatch = [regex]::Match($levelCode, 'new SurfaceChoppingBlock\((\d+),\s*(\d+)\)')
Draw-Sprite $axe 0 0 1 1 ([int]$axeMatch.Groups[1].Value) ([int]$axeMatch.Groups[2].Value)
$graphics.Dispose()
Add-Passage-Fade $canvas $true 10 11 $fadeCode
$canvas.Save((Join-Path $PSScriptRoot 'woodland-layout.png'), [System.Drawing.Imaging.ImageFormat]::Png)
$preview = [System.Drawing.Bitmap]::new($canvas.Width * 2, $canvas.Height * 2)
$graphics = [System.Drawing.Graphics]::FromImage($preview)
$graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
$graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
$graphics.DrawImage($canvas, 0, 0, $preview.Width, $preview.Height)
$graphics.Dispose()
$preview.Save((Join-Path $PSScriptRoot 'woodland-preview.png'), [System.Drawing.Imaging.ImageFormat]::Png)
foreach ($bitmap in @($terrain, $exterior, $furniture, $axe, $canvas, $preview)) { $bitmap.Dispose() }
Write-Output "Rendered static woodland artwork: $width x $height cells. Walls are flat; actors, lighting and fog are omitted."
