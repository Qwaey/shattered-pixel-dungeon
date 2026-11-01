# Деревянные стены поверхности

Дата создания: 2026-11-01.

По просьбе Автора обычные стены этажа 0 оформлены как дощатые стены
средневекового дома в пиксельной стилистике исходной игры. Для генерации
материала использован встроенный инструмент `image_gen`.

## Файлы и подключение

- [Лицевая текстура](../../core/src/main/assets/environment/surface_wood_wall.png):
  16×16 пикселей, непрозрачные вертикальные доски с тёмными швами и балкой.
- [Материал верхних граней](../../core/src/main/assets/environment/surface_wood_wall_cap.png):
  16×16 пикселей, повёрнутый и осветлённый вариант того же материала.
- [Набор поверхности](../../core/src/main/assets/environment/tiles_surface.png):
  256×256 пикселей, сетка тайлов 16×16.

`SurfaceLevel.tilesTex()` уже использует этот набор через
`Assets.Environment.TILES_SURFACE`; дополнительных изменений Java не требуется.
Текстуры подземельных этажей находятся в отдельных PNG.

| Часть обычной стены | Слоты в наборе, отсчёт от нуля |
| --- | --- |
| Плоское изображение и его вариант | 48, 52 |
| Лицевая сторона, включая торцы | 80–83, 96–99 |
| Стена за дверным проёмом | 88–91 |
| Верхние грани и углы | 144–159 |
| Нависающие края | 192–195 |

Всего обработаны 34 слота. Схему определяет `DungeonTileSheet.java`.
Чёрная внутренняя область полностью закрытой стены (слот 144) сохранена.
На этапе замены стен изображения декоративных стен, дверей и книжных шкафов
не перерабатывались. Позднее у обычных дверей поверхности также заменены
[каменные рамы и пол](surface-wood-doors.md).

## Подготовка материала

Сгенерированное изображение уменьшено до 16×16 точечной выборкой без
сглаживания: пиксель (x, y) берётся из координат
`floor(x * sourceWidth / 16), floor(y * sourceHeight / 16)`.
Такая привязка сохраняет тёмные швы между четырьмя досками.
Цвета приведены к восьми оттенкам: `#2b2119`, `#3d2d20`, `#503b28`,
`#644a30`, `#785b3a`, `#8e7049`, `#a28559`, `#b69a6d`.

Материал верхних граней получен поворотом лицевой текстуры на 90° по часовой
стрелке и осветлением в 1,12 раза с приведением к той же палитре.
В верхних четырёх строках лицевой стены использован материал верхней грани;
ниже — доски с затенением. Открытые торцы затемнены на две колонки по маске
варианта стены. Прозрачность каждого пикселя, полностью прозрачные пиксели
и чёрные внутренние области сохранены из исходного набора поверхности.
Пиксели вне перечисленных слотов, включая траву и лестницу, сохранены.

Клетки остаются `Terrain.WALL`: планировка, столкновения и правила горения
не менялись.

## Проверка ресурса

Проверены увеличенные изображения материалов и частей стен, размер обоих
материалов 16×16, непрозрачность материалов, размер набора 256×256 и палитра.
В наборе изменено 5764 пикселя внутри перечисленных слотов; альфа-канал,
чёрные внутренние области и все остальные пиксели совпадают с предыдущим
набором поверхности. Android-сборка `:android:assembleDebug` выполнена успешно;
три PNG в APK совпадают с исходными файлами. Игра не запускалась.

## Промпт генерации

```text
Use case: stylized-concept. Asset type: one production-ready tiny pixel-art wooden wall material tile for the top-down RPG Shattered Pixel Dungeon. Primary request: replace gray dungeon masonry with ordinary medieval house timber walls while matching the game's restrained, chunky low-resolution pixel art. Draw ONE square seamless panel of rough vertical wooden planks, viewed straight-on as a wall face, filling the entire canvas. Four broad boards with thin dark seams; hand-hewn, slightly uneven oak planks, sparse simple angular wood-grain marks, tiny restrained knots, warm muted medium-brown wood and deep dark-brown seams. A simple horizontal dark timber header along the top two logical pixel rows may help it read as medieval house construction. This image is an enlarged sprite that will be reduced to EXACTLY 16 by 16 game pixels: draw only a 16-by-16 logical pixel grid of large crisp solid-color blocks, with about 4 logical pixels across each board. Do not add finer-resolution details; no thin photorealistic wood grain. Use roughly eight flat colors, from #2b2119, #3d2d20, #503b28, #644a30, #785b3a, #8e7049, #a28559 to #b69a6d. Keep saturation subdued and shadows clear like Shattered Pixel Dungeon's terrain; matte aged wood rather than polished furniture. Simple vertical planks, shallow shading, minimal surface detail. Edges should repeat horizontally as continuous wooden house walls; no surrounding margin, background, text, palette swatches, grid lines, border, lettering, watermark, people, scenery, roof, stone, brick, plaster, doors, windows, metal fittings, modern siding, diagonal X braces, perspective, blur, smooth gradients, antialiasing, or illustrations of a whole house. Output only the one opaque wooden wall texture.
```
