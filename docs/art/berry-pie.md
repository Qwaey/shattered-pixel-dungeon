# Ягодный пирог Агафьи

Дата подготовки: 2026-11-04.

Автор выбрал ягодный пирог наградой за расширенный квест Агафьи. Внешний
вид должен быть очень похож на мясной пирог оригинала, питательность —
точно совпадать со стандартным пайком еды.

## Подготовленные ресурсы

- [Игровой спрайт](../../core/src/main/assets/sprites/berry_pie.png):
  прозрачный PNG 16×16, рисунок занимает прежний кадр 16×12.
- [Сравнение](berry-pie-comparison.png): мясной пирог слева, ягодный справа;
  увеличение 16× без сглаживания, клетчатый фон только для просмотра.
- [Референс](berry-pie-meat-reference.png): оригинальный слот `MEAT_PIE`
  из `sprites/items.png`, координаты (112, 432), увеличенный 32×.
- [Результат image_gen](berry-pie-generated.png): исходный результат
  встроенного инструмента, сохранённый в репозитории.
- [Подготовка спрайта](prepare-berry-pie.ps1): воспроизводимое извлечение
  трёх оттенков начинки и сборка конечного PNG.

2026-11-04 пирог подключён в согласованный расширенный квест как отдельный
`BerryPie extends Food`. Спрайт занимает новый слот `FOOD+13` (445),
награда выдаётся после воды, муки и ягод. Мясной пирог сохранён.
Подробности цепочки — в [описании квеста](berry-quest.md).

## Питательность предмета

Основа — обычный `items/food/Food.java`: `energy = Hunger.HUNGRY`, сейчас
300 единиц. Механика насыщения и применимые модификаторы остаются
такими же, как у стандартного пайка. `MeatPie` служит только образцом
рисунка: его 900 единиц питания и дополнительный `WellFed` не переносятся.

## Генерация и подготовка

Использован встроенный `image_gen`, без CLI/API. Из результата выбраны
три оттенка ягодной начинки: `#390a31`, `#440b36`, `#57123d`.
В оригинальном спрайте заменены только 37 пикселей открытой мясной начинки
трёх оттенков; их относительная светлота сохранена. Корочка, чёрный контур,
положение, размеры и альфа-канал исходного пирога сохранены целиком.
Так исключены изменения силуэта и лишние детали сгенерированного рисунка.
При последующем подключении пирога изменён только новый слот `FOOD+13`;
исходный слот мясного пирога не менялся.

Повторная подготовка из сохранённого результата:

```powershell
./docs/art/prepare-berry-pie.ps1
```

## Промпт

```text
Use case: precise-object-edit. Asset type: a tiny inventory item sprite for a Shattered Pixel Dungeon-based game. Image 1 is the exact original MEAT PIE sprite from the project's items atlas, enlarged 32 times with nearest-neighbor pixels: a 16 by 16 logical-pixel canvas, with the pie occupying 16 by 12 pixels at the top and transparent padding below. Create a BERRY PIE sibling sprite that looks extremely similar to this meat pie. Keep the same low, broad round pie shape, viewpoint, placement, outer silhouette, golden-brown baked pastry crust, dark pixel outline, large flat pixel clusters, limited palette, lighting, and empty transparent margins. Change only the exposed brown/red meat filling in the cut front-right part to dark purple and red-purple berry filling with a few subdued plum highlights; it must read as berry jam baked inside the same pastry. Do not redesign the pastry, add loose fruit, leaves, plate, utensils, steam, a new slice, lattice, extra holes, decoration, text, border, shadow outside the silhouette, or scenery. Match the coarse 16 by 16 logical pixel grid exactly: each logical pixel is a solid square block, no antialiasing, no gradients, no subpixel details, no blur. Output only one edited berry-pie sprite on a genuinely transparent background, with exactly the reference composition and its original transparent padding.
```

## Проверка

Осмотрено увеличенное сравнение с оригиналом. Проверены размер 16×16,
совпадение альфа-канала и всех пикселей вне 37 пикселей начинки. Java-код
не изменялся; сборка, автотесты и игра для этой подготовки не запускались.
