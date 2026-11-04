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
