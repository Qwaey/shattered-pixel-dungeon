# Спрайт Дровосека

Дата обновления: 2026-11-03.

По задаче Автора от 2026-11-03 персонажу дано имя Степан (`Stepan`).
Классы, пути текстур и сама анимация не менялись; ниже описана подготовка
его прежнего спрайта, который используется и после переименования.

По просьбе Автора рисунок переработан по спрайтам игровых героев оригинала:
широкая крупная голова, компактное тело и короткие ноги. Сохранены коричневые
волосы и борода, красная рабочая рубаха, тёмные штаны и топор.
Использован встроенный инструмент `image_gen` в режиме редактирования.

- [Игровая текстура](../../core/src/main/assets/sprites/lumberjack.png):
  PNG RGBA 32×16 пикселей, два соседних кадра 16×16.
- [Класс спрайта](../../core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/sprites/LumberjackSprite.java):
  `Assets.Sprites.LUMBERJACK`: основной кадр на 3,875 секунды и моргание
  на 0,125 секунды; полный цикл — 4 секунды.
- [Кадры героев для сравнения](lumberjack-hero-references.png):
  PNG 84×38 с первыми двумя кадрами каждого из шести классов.

## Исходные изображения

Основой финальной генерации послужили два настоящих кадра ожидания
[воина](../../core/src/main/assets/sprites/warrior.png). Размер кадра
в `HeroSprite` — 12×15 пикселей. Кадры 0 и 1 из первой строки помещены
в ячейки 16×16 с отступом 2 пикселя слева и увеличены в 24 раза
без сглаживания до 768×384. Это первое изображение в финальном вызове
`image_gen`; инструменту предложено сохранить пропорции головы, тела,
расположение глаз, носа и ног, заменив волосы, одежду и инструмент.

Второе изображение — увеличенная в 12 раз подборка исходных кадров:

| Ряд | Слева | По центру | Справа |
| --- | --- | --- | --- |
| Верхний | [Воин](../../core/src/main/assets/sprites/warrior.png) | [Маг](../../core/src/main/assets/sprites/mage.png) | [Разбойник](../../core/src/main/assets/sprites/rogue.png) |
| Нижний | [Охотница](../../core/src/main/assets/sprites/huntress.png) | [Дуэлянтка](../../core/src/main/assets/sprites/duelist.png) | [Клирик](../../core/src/main/assets/sprites/cleric.png) |

В подборке каждый класс занимает ячейку 28×19: исходные кадры 0 и 1
расположены рядом, с отступом (2, 2). Она использована для сравнения
характера пиксельных деталей, пропорций и оттенков. Спрайт торговца
в финальной генерации не использовался.

## Подготовка игрового листа

В первой версии листа использовались два отдельных рисунка из результата
`image_gen` 1774×887, уменьшенные до разной ширины (11×15 и 12×15).
Между ними отличались 133 пикселя, включая 7 пикселей силуэта.
При смене каждые полсекунды менялся весь персонаж; Автор сообщил о быстрой
анимации и впечатлении наложения кадров на карте и в разговоре.

После исправления оба кадра используют одну основу: второго персонажа
из области (987, 83, 575, 721), уменьшенного с масштабом 15/721
выборкой ближайших пикселей до 12×15. Он помещён с отступом (2, 0)
в ячейку 16×16, ноги находятся в строке 14, строка 15 прозрачная.
Альфа-канал и цвета основного рисунка сохранены.

Для моргания встроенный `image_gen` получил основной кадр, увеличенный
в 32 раза без сглаживания до 512×512. Из результата 1254×1254 взяты
только два цвета век в центрах логических пикселей (7, 4) и (10, 4).
Второй кадр — точная копия первого с этими двумя заменами; альфа-канал
сохранён даже у глаз. Поэтому тело, голова, топор и контур полностью
совпадают. Сглаживание не применялось.

`LumberjackSprite` использует 32 такта по 0,125 секунды: 31 раз кадр 0,
затем один раз кадр 1. Это редкое моргание, а не постоянная смена двух поз.

При проверке увеличенные кадры сопоставлены со спрайтами воина и клирика
при одинаковом размере пикселя. Игровые сценарии этим не проверялись.
Диалог, имя и размещение в доме в этой правке не менялись.

## Промпт выбранного варианта

```text
Use case: style-transfer / precise-object-edit. INPUT 1 is the EDIT TARGET: two ACTUAL ORIGINAL WARRIOR idle frames from Shattered Pixel Dungeon, padded into a 32x16 logical pixel atlas, enlarged by exactly 24x nearest-neighbour (768x384). Each half is a 16x16 logical cell with a native 12x15 hero centred inside. INPUT 2 is STYLE SUPPORT: actual idle frames of six original hero classes (Warrior, Mage, Rogue, Huntress, Duelist, Cleric). These are production pixel sprites, NOT illustrations.
Repaint the two Warrior frames in INPUT 1 as a friendly LUMBERJACK NPC. KEEP the Warrior's original anatomy and tiny pixel grammar: extremely broad oversized head, head almost as wide as shoulders, face in the upper half, compact torso, SHORT two-pixel legs, same height, same size, same positions of head/eyes/nose/feet, the same original low-resolution 12x15 style. It should read like an original additional game character at 1x scale. This is a costume and hair repaint of the actual game hero, NOT a new chibi illustration.
CHANGE ONLY these identity / outfit features: orange hair and beard become medium/dark brown, bare chest becomes a muted red work shirt with short rolled sleeves, white shorts become dark brown work trousers and brown boots, the small grey dagger at his side becomes a recognisable SMALL grey iron woodcutting axe on a brown handle in the same hand. Fit axe into the side margin without moving the body or changing the frame dimensions. Keep the head round and squat as in the original Warrior, short wide beard no lower than the original beard. Use two or three flat brown hair shades, simple warm skin patches with the Warrior's small shaded eye pixels, red shirt with a few flat shadow pixels. Keep the ORIGINAL hero palette brightness and soft brown outlines, NOT heavy black outlines, NOT shiny texture. Do NOT add a hat, suspenders, long beard, long arms, long shins, big glossy eyes, realistic anatomy, a long narrow head, high-resolution microdetails, or concept-art shading.
LAYOUT INVARIANTS: preserve the full 2:1 canvas and each character's position in INPUT 1. Exactly two frames, 32 columns and 16 rows TOTAL at logical native scale. Each square pixel is one existing grid cell from Input 1. The two frames are identical lumberjack identity, palette and head proportions, with only the small idle change already in the template. Hair can occupy rows 0-2, big head / face / beard rows 1-7, compact clothed body rows 8-11, short legs / boots rows 12-14. The foot baseline is row 14 and row 15 is empty. Render enlarged using precisely aligned uniform square pixel blocks, preferably 1024x512. Genuine transparent background; no white panels, backdrop, ground, shadows, captions, lettering, borders, grids or watermarks. Preserve the original sprite's simple shapes and the 32x16 logical resolution. Make it match the actual Warrior/Mage/Rogue/etc images supplied, not generic modern pixel art.
```

## Промпт моргания

```text
Use case: precise-object-edit. This is a production sprite correction, not a character redesign. Edit target: one 16x16 logical pixel lumberjack idle sprite enlarged exactly 32x nearest-neighbour to 512x512. Create the CLOSED-EYES BLINK variant of this EXACT sprite. Keep the head, brown hair, beard, face shape, red shirt, axe, body, feet, palette, transparent silhouette, pixel grid, size and position completely unchanged. Change ONLY the TWO black eye squares at logical pixel coordinates (7,4) and (10,4), counting from the top-left corner starting at zero: replace each open black eye with a muted brown closed eyelid matching the hero-style skin-shadow row around it. Do not move or repaint any other pixels; leave everything else bit-for-bit visually identical to the reference. No second character, no new pose, no animation sheet, no changed proportions, no added detail. Preserve the exact 16x16 logical grid and same framing, genuine transparent background with no ground/shadow, no text/watermark/grid, no antialiasing. Output the same single square sprite canvas enlarged with nearest-neighbour, ideally 512x512. The only desired difference is these two dark eye pixels becoming closed eyelids; the body must not move or morph.
```
