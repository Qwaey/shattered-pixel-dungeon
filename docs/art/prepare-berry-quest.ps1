$ErrorActionPreference = 'Stop'
if ($PSVersionTable.PSEdition -eq 'Core') {
    & "$env:WINDIR/System32/WindowsPowerShell/v1.0/powershell.exe" -NoProfile -ExecutionPolicy Bypass -File $PSCommandPath
    if ($LASTEXITCODE -ne 0) { throw 'Berry quest art preparation failed.' }
    return
}
Add-Type -AssemblyName System.Drawing
Add-Type -ReferencedAssemblies System.Drawing -TypeDefinition @'
using System;
using System.Drawing;
using System.Drawing.Imaging;
public static class BerryQuestArt {
    public static Bitmap Resize(Bitmap src, int width, int height) {
        var dst = new Bitmap(width, height, PixelFormat.Format32bppArgb);
        for (int y=0;y<height;y++) for (int x=0;x<width;x++)
            dst.SetPixel(x,y,src.GetPixel((int)((x+0.5)*src.Width/width),(int)((y+0.5)*src.Height/height)));
        return dst;
    }
    public static Bitmap Icon(string path, int width, int height) {
        using (var src=new Bitmap(path)) {
            int left=src.Width,top=src.Height,right=-1,bottom=-1;
            for (int y=0;y<src.Height;y++) for (int x=0;x<src.Width;x++) {
                if (src.GetPixel(x,y).A<192) continue;
                left=Math.Min(left,x);top=Math.Min(top,y);right=Math.Max(right,x);bottom=Math.Max(bottom,y);
            }
            if (right<left) throw new Exception("Empty generated icon: "+path);
            var dst=new Bitmap(16,16,PixelFormat.Format32bppArgb);
            int ox=(16-width)/2,oy=(16-height)/2;
            for (int y=0;y<height;y++) for (int x=0;x<width;x++)
                dst.SetPixel(ox+x,oy+y,src.GetPixel(left+(int)((x+0.5)*(right-left+1)/width),top+(int)((y+0.5)*(bottom-top+1)/height)));
            return dst;
        }
    }
    public static void Paste(Bitmap dst, Bitmap src, int ox, int oy) {
        for (int y=0;y<src.Height;y++) for (int x=0;x<src.Width;x++) dst.SetPixel(ox+x,oy+y,src.GetPixel(x,y));
    }
    public static void Save(Bitmap image, string path) { image.Save(path,ImageFormat.Png); }
    public static void Preview(Bitmap image, int scale, string path) {
        using (var enlarged=Resize(image,image.Width*scale,image.Height*scale)) Save(enlarged,path);
    }
    public static Bitmap PackItems(string path, Bitmap flour, Bitmap berries, string piePath) {
        using (var src=new Bitmap(path)) using (var pie=new Bitmap(piePath)) {
            var dst=new Bitmap(src.Width,src.Height,PixelFormat.Format32bppArgb);
            Paste(dst,src,0,0);
            Paste(dst,pie,208,432);
            Paste(dst,flour,224,464);
            Paste(dst,berries,240,464);
            return dst;
        }
    }
    private static Color Timber(Color c) {
        string[] hex={"2b2119","3d2d20","503b28","644a30","785b3a","8e7049","a28559","b69a6d"};
        int best=int.MaxValue; Color result=Color.Black;
        foreach (string h in hex) {
            Color p=ColorTranslator.FromHtml("#"+h);
            int d=(c.R-p.R)*(c.R-p.R)+(c.G-p.G)*(c.G-p.G)+(c.B-p.B)*(c.B-p.B);
            if (d<best) { best=d;result=p; }
        }
        return result;
    }
    public static Bitmap LockedDoors(string atlasPath, string originalPath, string generatedPath) {
        using (var src=new Bitmap(atlasPath)) using (var original=new Bitmap(originalPath))
        using (var generated=new Bitmap(generatedPath)) using (var art=Resize(generated,64,32)) {
            var dst=new Bitmap(src.Width,src.Height,PixelFormat.Format32bppArgb);
            Paste(dst,src,0,0);
            int[] slots={58,114,228,225,216,217,218,219};
            int[] normal={56,112,227,225,212,213,214,215};
            for (int i=0;i<slots.Length;i++) {
                if (i==3) continue;
                int ox=(slots[i]%16)*16,oy=(slots[i]/16)*16;
                int nx=(normal[i]%16)*16,ny=(normal[i]/16)*16;
                for (int y=0;y<16;y++) for (int x=0;x<16;x++) {
                    Color c=original.GetPixel(ox+x,oy+y),n=original.GetPixel(nx+x,ny+y);
                    int max=Math.Max(c.R,Math.Max(c.G,c.B)),min=Math.Min(c.R,Math.Min(c.G,c.B));
                    // Keep the original lock, leaf, metal bands, alpha and dark interior.
                    if (c.A==0 || c.ToArgb()!=n.ToArgb() || max<50 || max-min>50) continue;
                    Color wood=Timber(art.GetPixel((i%4)*16+x,(i/4)*16+y));
                    // Wooden floor already used by the ordinary raised doors.
                    if (slots[i]==114 && y>=12) wood=src.GetPixel(64+x,y);
                    dst.SetPixel(ox+x,oy+y,Color.FromArgb(c.A,wood));
                }
            }
            return dst;
        }
    }
}
'@
$taskRoot = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
$taskAssets = Join-Path $taskRoot 'core/src/main/assets'
$taskBushSource = [System.Drawing.Bitmap]::new((Join-Path $PSScriptRoot 'berry-bush-generated.png'))
$taskBush = [BerryQuestArt]::Resize($taskBushSource,64,32)
$taskFlour = [BerryQuestArt]::Icon((Join-Path $PSScriptRoot 'flour-sack-generated.png'),12,14)
$taskBerries = [BerryQuestArt]::Icon((Join-Path $PSScriptRoot 'pie-berries-generated.png'),12,12)
try {
    [BerryQuestArt]::Save($taskBush,(Join-Path $taskAssets 'environment/custom_tiles/surface_berry_bush.png'))
    [BerryQuestArt]::Save($taskFlour,(Join-Path $taskAssets 'sprites/flour_sack.png'))
    [BerryQuestArt]::Save($taskBerries,(Join-Path $taskAssets 'sprites/pie_berries.png'))
    $taskItemsPath = Join-Path $taskAssets 'sprites/items.png'
    $taskItems = [BerryQuestArt]::PackItems($taskItemsPath,$taskFlour,$taskBerries,(Join-Path $taskAssets 'sprites/berry_pie.png'))
    try { [BerryQuestArt]::Save($taskItems,"$taskItemsPath.berry.png") } finally { $taskItems.Dispose() }
    Move-Item -LiteralPath "$taskItemsPath.berry.png" -Destination $taskItemsPath -Force
    $taskDoorPath = Join-Path $taskAssets 'environment/tiles_surface.png'
    $taskDoors = [BerryQuestArt]::LockedDoors($taskDoorPath,(Join-Path $taskAssets 'environment/tiles_sewers.png'),(Join-Path $PSScriptRoot 'surface-locked-doors-generated.png'))
    try { [BerryQuestArt]::Save($taskDoors,"$taskDoorPath.berry.png") } finally { $taskDoors.Dispose() }
    Move-Item -LiteralPath "$taskDoorPath.berry.png" -Destination $taskDoorPath -Force
    $taskPreview = [System.Drawing.Bitmap]::new(96,32)
    try {
        [BerryQuestArt]::Paste($taskPreview,$taskBush,0,0)
        [BerryQuestArt]::Paste($taskPreview,$taskFlour,64,0)
        [BerryQuestArt]::Paste($taskPreview,$taskBerries,80,0)
        [BerryQuestArt]::Preview($taskPreview,8,(Join-Path $PSScriptRoot 'berry-quest-preview.png'))
    } finally { $taskPreview.Dispose() }
} finally {
    $taskBushSource.Dispose();$taskBush.Dispose();$taskFlour.Dispose();$taskBerries.Dispose()
}
Write-Output 'Prepared berry bush states, quest icons, item atlas and wooden locked-door frames.'
