# Деревянные дверные проёмы поверхности

Дата создания: 2026-11-01.

Автор заметил камень на фоне дверей в деревянных домах. Он находился в самих
дверных тайлах, которые были унаследованы от набора канализации.

## Ресурсы

- [Двери поверхности](../../core/src/main/assets/environment/surface_wood_doors.png):
  64×64 пикселя, 16 изображений по 16×16 в сетке 4×4.
- [Игровой набор поверхности](../../core/src/main/assets/environment/tiles_surface.png):
  256×256 пикселей. Исправленные части дверей помещены в прежние слоты;
  Java-код выбора тайлов и игровая логика не менялись.

Расположение тайлов в отдельном ресурсе, слева направо и сверху вниз:

```text
 56  57 112 113
116 224 225 227
208 209 210 211
212 213 214 215
```

Это обычные закрытая и открытая двери, их плоские изображения, пол под дверью
и верхние части для обоих направлений. Пустой слот 225 сохранён пустым.
Изображения специальных запертых и кристальных дверей не менялись.

## Подготовка

Использован встроенный инструмент `image_gen` в режиме редактирования.
Целью был составленный из исходных дверных тайлов лист 4×4, увеличенный
до 512×512 без сглаживания. Набор поверхности использован как образец
дерева и стиля. Из результата взяты только деревянные рамы, приведённые
к палитре стен: `#2b2119`, `#3d2d20`, `#503b28`, `#644a30`, `#785b3a`,
`#8e7049`, `#a28559`, `#b69a6d`.

При сборке сохранены исходные створки, прозрачность и чёрные области.
Светлые каменные рамы заменены по маске материала. В проёмах открытых дверей
и в нижних строках объёмных дверных тайлов пол взят из существующего
`FLOOR_SP` (слот 4); слот 116 целиком заменён тем же дощатым полом.
Остальные пиксели набора поверхности сохранены.

Правка является изменением графики и применяется также к сохранённым картам
поверхности после обновления приложения.

## Проверка

В наборе изменено 1918 пикселей внутри перечисленных слотов. Проверены
размеры 256×256 и 64×64, неизменность альфа-канала, створок и всех пикселей
вне дверных слотов. Слот 116 совпадает с существующим `FLOOR_SP`;
тайлы отдельного листа совпадают с игровым набором. Android-сборка
`:android:assembleDebug` выполнена успешно, оба PNG в APK совпадают
с исходными файлами. Игра не запускалась.

## Промпт редактирования

```text
Use case: precise-object-edit. Image 1 is the EDIT TARGET: a 4-by-4 sprite sheet containing 16 door-related tiles from Shattered Pixel Dungeon, each native tile 16 by 16 logical pixels, enlarged 8 times. Image 2 is a STYLE AND MATERIAL REFERENCE: the current surface atlas with muted brown wooden walls and an existing brown wooden plank floor in its top row. Replace ONLY the gray stone masonry surrounding the ordinary doors and the gray paving in their openings with wooden timber frames and wooden plank flooring matching Image 2. Preserve the original brown door leaf itself, black window openings, door orientation, silhouette, shadows, transparency, tile placement, and existing 4-by-4 sheet layout exactly. Both open and closed doors must have matching wooden frames; the open passage and floor-only tile should reveal brown horizontal wooden floorboards, not gray stone. Make the frame a simple squared dark-brown medieval house timber jamb and lintel, not recolored brickwork. Keep the low-resolution, restrained pixel-art style and hard pixel edges, small palette of muted dark-to-medium browns. Keep every original transparent pixel transparent and every black interior region black. The third tile in row 2 is transparent and must stay empty; do not invent doors for empty tiles. The small sideways brown doors should keep their original pixels, with only any remaining stone changed to wood. No new handles, locks, objects, text, labels, grid lines, extra borders, lighting effects, blur, gradients, antialiasing, or new scenery. Output only the edited sprite sheet with exactly the same square composition and sprite locations.
```
