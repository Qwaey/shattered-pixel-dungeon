$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing

$taskRepo = (Resolve-Path (Join-Path $PSScriptRoot '../..')).Path
$taskAtlasPath = Join-Path $taskRepo 'core/src/main/assets/sprites/items.png'
$taskOutputPath = Join-Path $taskRepo 'core/src/main/assets/sprites/berry_pie.png'
$taskGeneratedPath = Join-Path $PSScriptRoot 'berry-pie-generated.png'
$taskAtlas = [System.Drawing.Bitmap]::new($taskAtlasPath)
$taskGenerated = [System.Drawing.Bitmap]::new($taskGeneratedPath)
$taskPie = [System.Drawing.Bitmap]::new(16, 16)
$taskPreview = [System.Drawing.Bitmap]::new(544, 256)

try {
    # Only the exposed filling changes. Keep the exact original pastry and outline.
    $taskFilling = @('73512f', '815734', '8c6239')
    $taskSamples = @()
    for ($taskY = 0; $taskY -lt 12; $taskY++) {
        for ($taskX = 0; $taskX -lt 16; $taskX++) {
            $taskOriginal = $taskAtlas.GetPixel(112 + $taskX, 432 + $taskY)
            $taskHex = '{0:x2}{1:x2}{2:x2}' -f $taskOriginal.R, $taskOriginal.G, $taskOriginal.B
            if ($taskOriginal.A -eq 0 -or $taskHex -notin $taskFilling) { continue }
            $taskSample = $taskGenerated.GetPixel(
                [int][Math]::Floor(($taskX + 0.5) * $taskGenerated.Width / 16),
                [int][Math]::Floor(($taskY + 0.5) * $taskGenerated.Height / 16))
            if ($taskSample.A -ge 192 -and $taskSample.R -gt $taskSample.G * 1.5 -and $taskSample.B -gt $taskSample.G * 1.5) {
                $taskSamples += $taskSample
            }
        }
    }
    if ($taskSamples.Count -lt 3) { throw 'Generated berry filling is missing.' }
    $taskSamples = @($taskSamples | Sort-Object { 299 * $_.R + 587 * $_.G + 114 * $_.B })
    $taskPalette = @(
        $taskSamples[[int][Math]::Floor($taskSamples.Count / 6)],
        $taskSamples[[int][Math]::Floor($taskSamples.Count / 2)],
        $taskSamples[[int][Math]::Floor($taskSamples.Count * 5 / 6)])

    $taskChanged = 0
    for ($taskY = 0; $taskY -lt 16; $taskY++) {
        for ($taskX = 0; $taskX -lt 16; $taskX++) {
            $taskOriginal = $taskAtlas.GetPixel(112 + $taskX, 432 + $taskY)
            $taskColor = $taskOriginal
            $taskHex = '{0:x2}{1:x2}{2:x2}' -f $taskOriginal.R, $taskOriginal.G, $taskOriginal.B
            $taskShade = [array]::IndexOf($taskFilling, $taskHex)
            if ($taskOriginal.A -gt 0 -and $taskShade -ge 0) {
                $taskBerry = $taskPalette[$taskShade]
                $taskColor = [System.Drawing.Color]::FromArgb($taskOriginal.A, $taskBerry.R, $taskBerry.G, $taskBerry.B)
                $taskChanged++
            }
            $taskPie.SetPixel($taskX, $taskY, $taskColor)
        }
    }
    $taskPie.Save($taskOutputPath, [System.Drawing.Imaging.ImageFormat]::Png)

    for ($taskY = 0; $taskY -lt $taskPreview.Height; $taskY++) {
        for ($taskX = 0; $taskX -lt $taskPreview.Width; $taskX++) {
            $taskBackground = if (([int][Math]::Floor($taskX / 16) + [int][Math]::Floor($taskY / 16)) % 2 -eq 0) {
                [System.Drawing.Color]::FromArgb(95, 95, 95)
            } else {
                [System.Drawing.Color]::FromArgb(115, 115, 115)
            }
            $taskColor = $taskBackground
            $taskPixelY = [int][Math]::Floor($taskY / 16)
            if ($taskX -lt 256) {
                $taskPixel = $taskAtlas.GetPixel(112 + [int][Math]::Floor($taskX / 16), 432 + $taskPixelY)
                if ($taskPixel.A -gt 0) { $taskColor = $taskPixel }
            } elseif ($taskX -ge 288) {
                $taskPixel = $taskPie.GetPixel([int][Math]::Floor(($taskX - 288) / 16), $taskPixelY)
                if ($taskPixel.A -gt 0) { $taskColor = $taskPixel }
            }
            $taskPreview.SetPixel($taskX, $taskY, $taskColor)
        }
    }
    $taskPreview.Save((Join-Path $PSScriptRoot 'berry-pie-comparison.png'), [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Output ('Prepared berry_pie.png: 16x16, {0} filling pixels changed; original pastry, outline and alpha preserved.' -f $taskChanged)
    $taskPalette | ForEach-Object { Write-Output ('Berry shade: #{0:x2}{1:x2}{2:x2}' -f $_.R, $_.G, $_.B) }
} finally {
    $taskPreview.Dispose()
    $taskPie.Dispose()
    $taskGenerated.Dispose()
    $taskAtlas.Dispose()
}
