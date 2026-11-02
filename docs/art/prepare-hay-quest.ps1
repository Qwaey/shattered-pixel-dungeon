param([Parameter(Mandatory=$true)][string]$SourceDirectory, [switch]$MironOnly)
$ErrorActionPreference = 'Stop'
# Use the Windows GDI+ implementation consistently from either PowerShell host.
if ($PSVersionTable.PSEdition -eq 'Core') {
    $nativeArgs = @('-NoProfile', '-ExecutionPolicy', 'Bypass', '-File', $PSCommandPath, '-SourceDirectory', $SourceDirectory)
    if ($MironOnly) { $nativeArgs += '-MironOnly' }
    & "$env:WINDIR/System32/WindowsPowerShell/v1.0/powershell.exe" @nativeArgs
    if ($LASTEXITCODE -ne 0) { throw 'Asset preparation failed in Windows PowerShell.' }
    return
}
Add-Type -AssemblyName System.Drawing
Add-Type -ReferencedAssemblies System.Drawing -TypeDefinition @'
using System;
using System.Drawing;
using System.Drawing.Imaging;
public static class HayQuestArt {
    public static Bitmap Sprite(string path, Rectangle region, int w, int h) {
        using (var src = new Bitmap(path)) {
            if (region.Width == 0) region = new Rectangle(0, 0, src.Width, src.Height);
            int left=region.Right, top=region.Bottom, right=-1, bottom=-1;
            for (int y=region.Top; y<region.Bottom; y++) for (int x=region.Left; x<region.Right; x++) {
                if (src.GetPixel(x,y).A < 128) continue;
                left=Math.Min(left,x); right=Math.Max(right,x);
                top=Math.Min(top,y); bottom=Math.Max(bottom,y);
            }
            if (right < left) throw new Exception("Empty generated asset: "+path);
            var dst=new Bitmap(16,16,PixelFormat.Format32bppArgb);
            int dx=(16-w)/2, dy=15-h;
            string[] colors={"241c1c","342820","493528","5c4330","755239","906741",
                "ac824b","c29b55","dcc078","e8dbba","b8754f","d99464","efb783",
                "b0a681","72714c","575b39","3f482f","96927b","c4c6a0","e2e5c5",
                "6b6c62","a9b3a0","332b2b","514a3e","898064","b9a877","ede0aa"};
            for (int y=0;y<h;y++) for(int x=0;x<w;x++) {
                Color c=src.GetPixel(left+(int)((x+0.5)*(right-left+1)/w),top+(int)((y+0.5)*(bottom-top+1)/h));
                if(c.A<128) continue;
                int distance=int.MaxValue; Color chosen=Color.Transparent;
                foreach(string hex in colors) {
                    Color p=ColorTranslator.FromHtml("#"+hex);
                    int d=(p.R-c.R)*(p.R-c.R)+(p.G-c.G)*(p.G-c.G)+(p.B-c.B)*(p.B-c.B);
                    if(d<distance){distance=d;chosen=p;}
                }
                dst.SetPixel(dx+x,dy+y,chosen);
            }
            return dst;
        }
    }
    public static void Paste(Bitmap dst, Bitmap src, int ox, int oy) {
        for(int y=0;y<src.Height;y++) for(int x=0;x<src.Width;x++) dst.SetPixel(ox+x,oy+y,src.GetPixel(x,y));
    }
    public static void Save(Bitmap bitmap, string path) { bitmap.Save(path, ImageFormat.Png); }
}
'@
$repo = (Resolve-Path (Join-Path $PSScriptRoot '../..')).Path
$assets = Join-Path $repo 'core/src/main/assets'
$manifest = Get-Content -Raw (Join-Path $PSScriptRoot 'hay-quest-generation.json') | ConvertFrom-Json
$props = Join-Path $assets 'environment/hay-quest'
[IO.Directory]::CreateDirectory($props) | Out-Null
$sprites = @{}
foreach ($entry in $manifest.assets) {
    if ($MironOnly -and $entry.name -ne 'miron') { continue }
    $source = Join-Path $SourceDirectory $entry.source
    if ($entry.name -eq 'senogryz') {
        $original = [Drawing.Bitmap]::new($source)
        $qw = [int][Math]::Floor($original.Width / 2)
        $qh = [int][Math]::Floor($original.Height / 2)
        $original.Dispose()
        $sheet = [Drawing.Bitmap]::new(64,16)
        for ($i=0; $i -lt 4; $i++) {
            $region = [Drawing.Rectangle]::new(($i % 2)*$qw,[int][Math]::Floor($i/2)*$qh,$qw,$qh)
            $frame = [HayQuestArt]::Sprite($source,$region,14,12)
            [HayQuestArt]::Paste($sheet,$frame,$i*16,0)
            $frame.Dispose()
        }
        [HayQuestArt]::Save($sheet,(Join-Path $assets 'sprites/senogryz.png'))
        $sheet.Dispose()
        continue
    }
    $w=14; $h=12
    switch ($entry.name) {
        'miron' {$w=12; $h=15}
        'rake' {$h=14}
        'shell' {$h=10}
        'eggs' {$h=11}
        'rope' {$w=12; $h=7}
        'tracks' {$h=14}
        'refuge' {$h=10}
    }
    $sprite = [HayQuestArt]::Sprite($source,[Drawing.Rectangle]::Empty,$w,$h)
    $sprites[$entry.name]=$sprite
    $path = if ($entry.name -eq 'miron') {Join-Path $assets 'sprites/miron.png'} else {Join-Path $props ($entry.name+'.png')}
    [HayQuestArt]::Save($sprite,$path)
}
if (-not $MironOnly) {
$atlas = [Drawing.Bitmap]::new(128,16)
$order = @('rope','shell','eggs','empty_box','box','tracks','refuge')
for($i=0;$i -lt $order.Count;$i++){[HayQuestArt]::Paste($atlas,$sprites[$order[$i]],$i*16,0)}
[HayQuestArt]::Save($atlas,(Join-Path $assets 'environment/custom_tiles/hay_quest.png'))
$atlas.Dispose()
# Only the three previously unused quest slots change in the item atlas.
$itemsPath = Join-Path $assets 'sprites/items.png'
$loaded = [Drawing.Bitmap]::new($itemsPath)
$items = [Drawing.Bitmap]::new($loaded.Width,$loaded.Height)
[HayQuestArt]::Paste($items,$loaded,0,0)
$loaded.Dispose()
[HayQuestArt]::Paste($items,$sprites['empty_box'],0,464)
[HayQuestArt]::Paste($items,$sprites['rake'],224,464)
[HayQuestArt]::Paste($items,$sprites['box'],240,464)
[HayQuestArt]::Save($items,$itemsPath)
$items.Dispose()
}
# A static native-pixel preview, enlarged without interpolation for visual review.
$preview = [Drawing.Bitmap]::new(16*11,16)
[HayQuestArt]::Paste($preview,$sprites['miron'],0,0)
$beetle=[Drawing.Bitmap]::new((Join-Path $assets 'sprites/senogryz.png'))
[HayQuestArt]::Paste($preview,$beetle,16,0)
$beetle.Dispose()
$i=5
foreach($name in @('rake','shell','eggs','empty_box','box','refuge')) {
    if (-not $sprites.ContainsKey($name)) { $sprites[$name] = [Drawing.Bitmap]::new((Join-Path $props ($name+'.png'))) }
    [HayQuestArt]::Paste($preview,$sprites[$name],$i*16,0)
    $i++
}
$large = [Drawing.Bitmap]::new($preview.Width*6,96)
for($y=0;$y -lt 96;$y++){for($x=0;$x -lt $large.Width;$x++){$large.SetPixel($x,$y,$preview.GetPixel([int][Math]::Floor($x/6),[int][Math]::Floor($y/6)))}}
[HayQuestArt]::Save($large,(Join-Path $PSScriptRoot 'hay-quest-preview.png'))
$preview.Dispose(); $large.Dispose()
foreach($sprite in $sprites.Values){$sprite.Dispose()}
if ($MironOnly) { Write-Output 'Updated only Miron and the static preview.' }
else { Write-Output 'Prepared Miron, four Senogryz frames, eight props, floor atlas, three item slots and static preview.' }
