# Текстура травы поверхности

Дата создания: 2026-11-01.

По просьбе Автора создана обычная короткая трава в стилистике существующих
пиксельных текстур игры. Использован встроенный инструмент `image_gen`.

- [Отдельный тайл](../../core/src/main/assets/environment/surface_grass.png):
  16×16 пикселей, полностью непрозрачный PNG.
- [Набор поверхности](../../core/src/main/assets/environment/tiles_surface.png):
  256×256 пикселей, сетка 16×16 тайлов по 16×16 пикселей.

Из сгенерированного изображения выбран верхний левый квадратный фрагмент
размером в треть стороны. Он уменьшен до 16×16 выборкой ближайших пикселей;
цвета приведены к ограниченной палитре `#35452c`, `#405735`, `#4d633a`,
`#5c7140`, `#6e8047`, `#2d3d29`. Сглаживание не применялось.

Набор поверхности основан на `tiles_sewers.png`. Новый тайл помещён в слоты
`FLOOR` (0), `FLOOR_ALT_1` (6) и `FLOOR_ALT_2` (12): координаты левого
верхнего угла — (0, 0), (96, 0) и (192, 0) пикселей. Остальные пиксели
набора сохранены, включая лестницу. `SurfaceLevel` использует новый набор;
тип обычного пола остаётся `Terrain.EMPTY`.

## Промпт генерации

```text
Use case: stylized-concept. Asset type: one seamless, opaque 16-by-16-pixel grass ground tile for a top-down RPG. Primary request: create ordinary short grass matching the restrained pixel-art terrain style of Shattered Pixel Dungeon. Produce a single square tile filling the entire canvas, shown as a nearest-neighbor enlargement of EXACTLY 16 columns by 16 rows of logical pixels. Each logical pixel must be a uniform solid-color square; no details smaller than one-sixteenth of canvas width. The tile should read as a quiet, continuous grassy ground surface, not a tall bush or separate clump: a muted dark olive-green midtone base with a few sparse small 1-to-3-pixel tufts and broken angular blade clusters, subdued lighter olive highlights and darker green shadow pixels. Use a small flat palette, roughly six colors around #35452c, #405735, #4d633a, #5c7140, #6e8047 and #2d3d29. Keep contrast and saturation restrained like the game's gray stone and olive dungeon vegetation. Orthographic top-down view, evenly lit, no perspective, no ambient cast shadows. Opposite edges must tile seamlessly with no dark frame or obvious grid, no central object, no flowers, stones, dirt paths, characters, text, labels, border, watermark, blur, gradients, anti-aliasing, outlines, or tiny photographic detail. Output just the one grass texture square, edge to edge.
```
