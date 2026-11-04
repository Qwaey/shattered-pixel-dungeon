# Shared ground sampling for static artwork previews, without launching the game.
function Route-Samples([int] $width, [int] $height, [object[]] $routes, [string[]] $wear = @()) {
    $samples = [bool[,]]::new($width + 1, $height + 1)
    for ($y = 0; $y -le $height; $y++) {
        for ($x = 0; $x -le $width; $x++) {
            if ($wear.Count -gt 0 -and $wear[$y % $wear.Count][$x % $wear[0].Length] -ne '#') { continue }
            foreach ($route in $routes) {
                for ($i = 0; $i -lt $route.Count - 2; $i += 2) {
                    $dx = $route[$i + 2] - $route[$i]
                    $dy = $route[$i + 3] - $route[$i + 1]
                    $along = [Math]::Clamp([double]((($x - $route[$i] - 0.5) * $dx + ($y - $route[$i + 1] - 0.5) * $dy) / ($dx * $dx + $dy * $dy)), [double]0, [double]1)
                    $offsetX = $x - $route[$i] - 0.5 - $along * $dx
                    $offsetY = $y - $route[$i + 1] - 0.5 - $along * $dy
                    if ($offsetX * $offsetX + $offsetY * $offsetY -le 0.85 * 0.85) { $samples[$x, $y] = $true }
                }
            }
        }
    }
    return ,$samples
}

# Static illustration of the programmatic overlay, drawn after the tree crowns.
function Add-Passage-Fade($canvas, [bool] $north, [int] $left, [int] $right, [string] $effectCode) {
    $depth = [int][regex]::Match($effectCode, 'DEPTH\s*=\s*(\d+)').Groups[1].Value
    $coreMargin = [double][regex]::Match($effectCode, 'CORE_MARGIN\s*=\s*([\d.]+)').Groups[1].Value
    $feather = [double][regex]::Match($effectCode, 'FEATHER\s*=\s*([\d.]+)').Groups[1].Value * 16
    $fadeWidth = ($right - $left + 1 + 2 * $coreMargin) * 16 + 2 * $feather
    $fadeHeight = $depth * 16
    $startX = ($left - $coreMargin) * 16 - $feather
    $startY = if ($north) { 0 } else { $canvas.Height - $fadeHeight }
    function Smooth-Step([double] $value) {
        $value = [Math]::Clamp($value, [double]0, [double]1)
        return $value * $value * (3 - 2 * $value)
    }
    for ($y = 0; $y -lt $fadeHeight; $y++) {
        $distance = if ($north) { $y + 0.5 } else { $fadeHeight - $y - 0.5 }
        $depthAlpha = Smooth-Step (($fadeHeight - $distance) / ($fadeHeight - 16))
        for ($x = 0; $x -lt $fadeWidth; $x++) {
            $worldX = [int]$startX + $x
            $worldY = [int]$startY + $y
            if ($worldX -lt 0 -or $worldX -ge $canvas.Width -or $worldY -lt 0 -or $worldY -ge $canvas.Height) { continue }
            $alpha = $depthAlpha * (Smooth-Step (($x + 0.5) / $feather)) * (Smooth-Step (($fadeWidth - $x - 0.5) / $feather))
            $color = $canvas.GetPixel($worldX, $worldY)
            $canvas.SetPixel($worldX, $worldY, [System.Drawing.Color]::FromArgb($color.A, [int]($color.R * (1 - $alpha)), [int]($color.G * (1 - $alpha)), [int]($color.B * (1 - $alpha))))
        }
    }
}
