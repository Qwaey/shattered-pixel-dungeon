# Ресурсы квеста «Застрявшая телега»

Дата: 2026-11-01.

Для квеста Дровосека встроенным `image_gen` создан один новый ресурс —
крепкая дубовая доска с прозрачным фоном. CLI и внешние API не использовались.

- [Доска](../../core/src/main/assets/sprites/cart_plank.png): PNG RGBA 16×16,
  рисунок 14×12 в левом верхнем углу. Используется свободный слот `QUEST+13`
  в `sprites/items.png`, прямоугольник (208, 464, 16, 16).
- [Атлас телеги](../../core/src/main/assets/environment/custom_tiles/surface_handcart.png):
  PNG RGBA 96×16, три кадра 32×16: застрявшая, с доской под колесом,
  откаченная на площадку.
- [Увеличенное сравнение](cart-quest-preview.png): все три кадра ×8,
  без сглаживания.

## Подготовка

Границы сгенерированной доски найдены по альфа-каналу с порогом 128.
Рисунок уменьшен до 14×12 выборкой центрального исходного пикселя каждой
целевой ячейки. Цвета приведены к восьми коричневым оттенкам; альфа бинарная.
Остальная часть PNG 16×16 прозрачная. При добавлении в атлас проверено,
что все пиксели за пределами нового слота, включая значки ведра, сохранены.

Основой каждого кадра телеги служит существующий рисунок из
`surface_exterior.png`, прямоугольник (160, 32, 32, 16). Сам рисунок телеги
не менялся: при сборке кадров под левым колесом добавлена небольшая тёмная
колея, а во втором кадре — уменьшенный рисунок доски. Третий кадр использует
исходную телегу без колеи и доски. Исходный атлас экстерьера сохранён.

## Планировка

Северо-восточный дом сдвинут из (54, 42) в (50, 42), с мебелью и дверью.
Проход x = 48–49 между северными домами перекрыт телегой в y = 44.
Доска лежит в стандартном незапертом сундуке в (56, 45), у восточной
стены правого дома. Сундук использует существующие графику и механику
`Heap.Type.CHEST`; отдельная текстура для него не создавалась.
После выполнения действий телега стоит в (46–47, 40), слева от колодца;
старое место становится проходимым. Яблоня перенесена в (48, 37).
Все координаты считаются от нуля. Состояния и положение сохраняются
в `SurfaceHandcart`; размещение создаётся только вместе с новой поверхностью.
Осмотр телеги через лупу добавляет заметку в журнал; перемещение удаляет её.
Работать с телегой можно без предварительного осмотра или разговора.
Отдельной темы у Дровосека, принятия задания и выдачи еды нет: результатом
становится свободный проход. Тексты дают косвенные подсказки, не перечисляя
шаги решения и нажатия интерфейса.

## Промпт доски

Вызов встроенного `image_gen`, `transparent_background: true`:

```text
Use case: stylized-concept. Asset type: one production pixel-art inventory item for a medieval RPG based on Shattered Pixel Dungeon. Primary request: a SINGLE thick short rough wooden plank, lying diagonally from bottom left to top right, viewed slightly from above. One solid oak board, warm muted ochre brown, dark brown one-pixel outline, two or three simple grain streaks, thick enough to support a cart wheel. Exact tiny low-resolution pixel-art grammar, suitable for a 16x16 pixel item atlas cell. Treat canvas as EXACTLY a 16 by 16 logical pixel grid, draw silhouette within 14 by 12 logical pixels centered with transparent margins. Enlarge each logical pixel as a uniform square block to fill the output canvas. About six flat solid colours, hard square pixel edges, NO antialiasing, no fine realistic wood texture, no shadow, no floor, no additional objects, no text, no grid. Genuine transparent background. The board must be clearly recognizable at 16x16 resolution.
```

## Проверка

Проверены размеры, прозрачность, увеличение трёх кадров и сохранность
существующих пикселей предметного атласа. Прохождение квеста в игре
в рамках подготовки ресурсов не проверялось.
