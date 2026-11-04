# Static comparison: southern village passage on the left, northern woodland on the right.
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$village = [System.Drawing.Bitmap]::new((Join-Path $PSScriptRoot 'surface-paths-layout.png'))
$woodland = [System.Drawing.Bitmap]::new((Join-Path $PSScriptRoot 'woodland-layout.png'))
$preview = [System.Drawing.Bitmap]::new(976, 480)
$graphics = [System.Drawing.Graphics]::FromImage($preview)
$graphics.Clear([System.Drawing.Color]::FromArgb(28, 29, 24))
$graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
$graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
$graphics.DrawImage($village, [System.Drawing.Rectangle]::new(0, 0, 480, 480), [System.Drawing.Rectangle]::new(24 * 16, $village.Height - 160, 160, 160), [System.Drawing.GraphicsUnit]::Pixel)
$graphics.DrawImage($woodland, [System.Drawing.Rectangle]::new(496, 0, 480, 480), [System.Drawing.Rectangle]::new(6 * 16, 0, 160, 160), [System.Drawing.GraphicsUnit]::Pixel)
$graphics.Dispose()
$preview.Save((Join-Path $PSScriptRoot 'forest-passages.png'), [System.Drawing.Imaging.ImageFormat]::Png)
foreach ($bitmap in @($village, $woodland, $preview)) { $bitmap.Dispose() }
Write-Output 'Rendered both forest passage approaches without launching the game.'
