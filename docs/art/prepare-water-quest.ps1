param([switch]$EmptyOnly)
$ErrorActionPreference = 'Stop'
if ($PSVersionTable.PSEdition -eq 'Core') {
    $taskArgs = @('-NoProfile','-ExecutionPolicy','Bypass','-File',$PSCommandPath)
    if ($EmptyOnly) { $taskArgs += '-EmptyOnly' }
    & "$env:WINDIR/System32/WindowsPowerShell/v1.0/powershell.exe" @taskArgs
    if ($LASTEXITCODE -ne 0) { throw 'Water quest art preparation failed.' }
    return
}
Add-Type -AssemblyName System.Drawing
Add-Type -ReferencedAssemblies System.Drawing -TypeDefinition @'
using System;
using System.Drawing;
using System.Drawing.Imaging;
public static class VillageArt {
    private static readonly Color[] Palette=Array.ConvertAll(new string[]{
        "2c170e","4d2c15","754521","a4682b","c88c3d","e2ae59","f4c96b",
        "45494f","6a707b","9ba4af","d1d7d7"}, h=>ColorTranslator.FromHtml("#"+h));
    private static readonly Color[] WaterPalette=Array.ConvertAll(new string[]{
        "174d72","2b779b","4aa6c1","89d2df"}, h=>ColorTranslator.FromHtml("#"+h));
    private static Color Nearest(Color c, Color[] palette) {
        Color best=palette[0]; int distance=int.MaxValue;
        foreach(Color p in palette) {
            int d=(p.R-c.R)*(p.R-c.R)+(p.G-c.G)*(p.G-c.G)+(p.B-c.B)*(p.B-c.B);
            if(d<distance) { distance=d; best=p; }
        }
        return best;
    }
    public static Bitmap Bucket(string path) {
        using(var src=new Bitmap(path)) {
            int left=src.Width,top=src.Height,right=-1,bottom=-1;
            for(int y=0;y<src.Height;y++) for(int x=0;x<src.Width;x++) {
                if(src.GetPixel(x,y).A<192) continue;
                left=Math.Min(left,x);right=Math.Max(right,x);top=Math.Min(top,y);bottom=Math.Max(bottom,y);
            }
            if(right<left) throw new Exception("Empty bucket artwork.");
            var dst=new Bitmap(16,16,PixelFormat.Format32bppArgb);
            for(int y=0;y<12;y++) for(int x=0;x<14;x++) {
                Color c=src.GetPixel(left+(int)((x+0.5)*(right-left+1)/14),top+(int)((y+0.5)*(bottom-top+1)/12));
                if(c.A>=192) dst.SetPixel(x+1,y+3,Nearest(c,Palette));
            }
            return dst;
        }
    }
    public static Color SampleCell(Bitmap src,int x,int y) {
        return src.GetPixel((int)((x+0.5)*src.Width/16),(int)((y+0.5)*src.Height/16));
    }
    public static Bitmap Blink(Bitmap original,string path) {
        var blink=new Bitmap(original.Width,original.Height,PixelFormat.Format32bppArgb);
        Paste(blink,original,0,0);
        using(var src=new Bitmap(path)) {
            foreach(int x in new int[]{8,11}) {
                Color c=SampleCell(src,x,5);
                if(c.A<192) throw new Exception("Blink eye became transparent.");
                // Keep the existing palette and every non-eye pixel of Agafya.
                Color[] skin={original.GetPixel(8,6),original.GetPixel(11,6)};
                blink.SetPixel(x,5,Nearest(c,skin));
            }
        }
        return blink;
    }
    public static bool IsWater(int x,int y) {
        return (y==5 || y==6) && x>=4 && x<=11;
    }
    public static Bitmap FilledBucket(Bitmap empty,string path) {
        var filled=new Bitmap(empty.Width,empty.Height,PixelFormat.Format32bppArgb);
        Paste(filled,empty,0,0);
        using(var src=new Bitmap(path)) {
            int left=src.Width,top=src.Height,right=-1,bottom=-1;
            // Search only the opening, excluding any blue fringes on the outer silhouette.
            for(int y=src.Height*4/16;y<src.Height*8/16;y++) for(int x=src.Width*3/16;x<src.Width*13/16;x++) {
                Color c=src.GetPixel(x,y);
                if(c.A<192 || c.B<=c.R+24 || c.G<=c.R+12) continue;
                left=Math.Min(left,x);right=Math.Max(right,x);top=Math.Min(top,y);bottom=Math.Max(bottom,y);
            }
            if(right<left) throw new Exception("Generated water is missing.");
            for(int y=0;y<16;y++) for(int x=0;x<16;x++) {
                if(!IsWater(x,y)) continue;
                Color c=src.GetPixel(left+(int)((x-4+0.5)*(right-left+1)/8),top+(int)((y-5+0.5)*(bottom-top+1)/2));
                if(c.A<192 || c.B<=c.R || c.G<=c.R) throw new Exception("Missing generated blue water at "+x+","+y);
                if(empty.GetPixel(x,y).A==0) throw new Exception("Water escaped the bucket silhouette.");
                filled.SetPixel(x,y,Nearest(c,WaterPalette));
            }
        }
        return filled;
    }
    public static void Paste(Bitmap dst,Bitmap src,int ox,int oy) {
        for(int y=0;y<src.Height;y++) for(int x=0;x<src.Width;x++) dst.SetPixel(ox+x,oy+y,src.GetPixel(x,y));
    }
    public static void Save(Bitmap src,string path) { src.Save(path,ImageFormat.Png); }
    public static void Preview(Bitmap src,int scale,string path) {
        using(var dst=new Bitmap(src.Width*scale,src.Height*scale)) {
            for(int y=0;y<dst.Height;y++) for(int x=0;x<dst.Width;x++) dst.SetPixel(x,y,src.GetPixel(x/scale,y/scale));
            Save(dst,path);
        }
    }
    public static void PackBuckets(string path,Bitmap empty,Bitmap filled) {
        using(var original=new Bitmap(path)) using(var atlas=new Bitmap(original.Width,original.Height,PixelFormat.Format32bppArgb)) {
            Paste(atlas,original,0,0);
            for(int y=464;y<480;y++) for(int x=176;x<208;x++) atlas.SetPixel(x,y,Color.FromArgb(0,0,0,0));
            for(int y=0;y<12;y++) for(int x=0;x<14;x++) {
                atlas.SetPixel(176+x,464+y,empty.GetPixel(x+1,y+3));
                atlas.SetPixel(192+x,464+y,filled.GetPixel(x+1,y+3));
            }
            for(int y=0;y<atlas.Height;y++) for(int x=0;x<atlas.Width;x++) {
                if(y>=464 && y<480 && x>=176 && x<208) continue;
                if(original.GetPixel(x,y).ToArgb()!=atlas.GetPixel(x,y).ToArgb()) throw new Exception("Unrelated item pixel changed.");
            }
            original.Dispose(); Save(atlas,path);
        }
    }
}
'@
$repo = (Resolve-Path (Join-Path $PSScriptRoot '../..')).Path
$assets = Join-Path $repo 'core/src/main/assets'
$empty = [VillageArt]::Bucket((Join-Path $PSScriptRoot 'bucket-empty-generated.png'))
try {
    [VillageArt]::Save($empty,(Join-Path $assets 'sprites/bucket.png'))
    [VillageArt]::Preview($empty,32,(Join-Path $PSScriptRoot 'bucket-reference.png'))
    [VillageArt]::Preview($empty,12,(Join-Path $PSScriptRoot 'bucket-native-preview.png'))
    if ($EmptyOnly) { Write-Output 'Prepared the wide empty bucket and water-edit reference.'; return }
    $filled = [VillageArt]::FilledBucket($empty,(Join-Path $PSScriptRoot 'bucket-water-generated.png'))
    try {
        [VillageArt]::Save($filled,(Join-Path $assets 'sprites/bucket_water.png'))
        [VillageArt]::PackBuckets((Join-Path $assets 'sprites/items.png'),$empty,$filled)
        $original = [Drawing.Bitmap]::new((Join-Path $PSScriptRoot 'agafya-before-animation.png'))
        $blink = [VillageArt]::Blink($original,(Join-Path $PSScriptRoot 'agafya-blink-generated.png'))
        $sheet = [Drawing.Bitmap]::new(32,16)
        $stepan = [Drawing.Bitmap]::new((Join-Path $assets 'sprites/lumberjack.png'))
        $preview = [Drawing.Bitmap]::new(80,16)
        try {
            [VillageArt]::Paste($sheet,$original,0,0)
            [VillageArt]::Paste($sheet,$blink,16,0)
            [VillageArt]::Save($sheet,(Join-Path $assets 'sprites/grandmother.png'))
            [VillageArt]::Paste($preview,$sheet,0,0)
            for($y=0;$y -lt 16;$y++) { for($x=0;$x -lt 16;$x++) { $preview.SetPixel(32+$x,$y,$stepan.GetPixel($x,$y)) } }
            [VillageArt]::Paste($preview,$empty,48,0)
            [VillageArt]::Paste($preview,$filled,64,0)
            [VillageArt]::Preview($preview,10,(Join-Path $PSScriptRoot 'water-quest-preview.png'))
        } finally { $original.Dispose();$blink.Dispose();$sheet.Dispose();$stepan.Dispose();$preview.Dispose() }
    } finally { $filled.Dispose() }
} finally { $empty.Dispose() }
Write-Output 'Prepared Agafya blink, matching bucket states, two item slots and the static preview.'
