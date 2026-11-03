param([string]$MironSource = (Join-Path $PSScriptRoot 'miron-idle-generated.png'))
$ErrorActionPreference = 'Stop'
if ($PSVersionTable.PSEdition -eq 'Core') {
    & "$env:WINDIR/System32/WindowsPowerShell/v1.0/powershell.exe" -NoProfile -ExecutionPolicy Bypass -File $PSCommandPath -MironSource $MironSource
    if ($LASTEXITCODE -ne 0) { throw 'Asset preparation failed in Windows PowerShell.' }
    return
}
Add-Type -AssemblyName System.Drawing
Add-Type -ReferencedAssemblies System.Drawing -TypeDefinition @'
using System;
using System.Drawing;
using System.Drawing.Imaging;
public static class HayQuestArt {
    public static Bitmap Frame(Bitmap src, int index) {
        int half = src.Width / 2;
        int left=(index+1)*half, top=src.Height, right=-1, bottom=-1;
        for(int y=0;y<src.Height;y++) for(int x=index*half;x<(index+1)*half;x++) {
            if(src.GetPixel(x,y).A<192) continue;
            left=Math.Min(left,x); right=Math.Max(right,x);
            top=Math.Min(top,y); bottom=Math.Max(bottom,y);
        }
        if(right<left) throw new Exception("Empty Miron frame.");
        string[] hex={"241c1c","342820","493528","5c4330","755239","906741",
            "ac824b","c29b55","dcc078","e8dbba","b8754f","d99464","efb783",
            "b0a681","72714c","575b39","3f482f","96927b","c4c6a0","e2e5c5",
            "6b6c62","a9b3a0","332b2b","514a3e","898064","b9a877","ede0aa"};
        Color[] palette=Array.ConvertAll(hex, h => ColorTranslator.FromHtml("#"+h));
        var dst=new Bitmap(16,16,PixelFormat.Format32bppArgb);
        for(int y=0;y<15;y++) for(int x=0;x<12;x++) {
            // Sample below row centres so the generated dark eye row survives at 1x.
            Color c=src.GetPixel(left+(int)((x+0.5)*(right-left+1)/12),top+(int)((y+0.85)*(bottom-top+1)/15));
            if(c.A<192) continue;
            int distance=int.MaxValue; Color chosen=Color.Transparent;
            foreach(Color p in palette) {
                int d=(p.R-c.R)*(p.R-c.R)+(p.G-c.G)*(p.G-c.G)+(p.B-c.B)*(p.B-c.B);
                if(d<distance) { distance=d; chosen=p; }
            }
            dst.SetPixel(x+2,y,chosen);
        }
        return dst;
    }
    public static void Paste(Bitmap dst, Bitmap src, int ox, int oy) {
        for(int y=0;y<src.Height;y++) for(int x=0;x<src.Width;x++) dst.SetPixel(ox+x,oy+y,src.GetPixel(x,y));
    }
    public static void Save(Bitmap bitmap, string path) { bitmap.Save(path,ImageFormat.Png); }
    public static void Preview(Bitmap bitmap, int scale, string path) {
        using(var dst=new Bitmap(bitmap.Width*scale,bitmap.Height*scale)) {
            for(int y=0;y<dst.Height;y++) for(int x=0;x<dst.Width;x++) dst.SetPixel(x,y,bitmap.GetPixel(x/scale,y/scale));
            Save(dst,path);
        }
    }
}
'@
$repo = (Resolve-Path (Join-Path $PSScriptRoot '../..')).Path
$assets = Join-Path $repo 'core/src/main/assets'
$source = [Drawing.Bitmap]::new((Resolve-Path $MironSource).Path)
try {
    $neutral = [HayQuestArt]::Frame($source,0)
    $turned = [HayQuestArt]::Frame($source,1)
} finally { $source.Dispose() }
try {
    # Register both poses to the same body; only the head and hat animate.
    for($y=8;$y -lt 16;$y++) { for($x=0;$x -lt 16;$x++) { $turned.SetPixel($x,$y,$neutral.GetPixel($x,$y)) } }
    $sheet = [Drawing.Bitmap]::new(32,16)
    try {
        [HayQuestArt]::Paste($sheet,$neutral,0,0)
        [HayQuestArt]::Paste($sheet,$turned,16,0)
        [HayQuestArt]::Save($sheet,(Join-Path $assets 'sprites/miron.png'))
        [HayQuestArt]::Preview($sheet,12,(Join-Path $PSScriptRoot 'miron-idle-preview.png'))
        $beetle = [Drawing.Bitmap]::new((Join-Path $assets 'sprites/senogryz.png'))
        $preview = [Drawing.Bitmap]::new(96,16)
        try {
            [HayQuestArt]::Paste($preview,$sheet,0,0)
            [HayQuestArt]::Paste($preview,$beetle,32,0)
            [HayQuestArt]::Preview($preview,8,(Join-Path $PSScriptRoot 'hay-quest-preview.png'))
        } finally { $beetle.Dispose(); $preview.Dispose() }
    } finally { $sheet.Dispose() }
} finally { $neutral.Dispose(); $turned.Dispose() }
Write-Output 'Prepared two 16x16 Miron frames and static previews; Senogryz is unchanged.'
