# Квест «Ягодный пирог Агафьи»

Дата: 2026-11-04.

По согласованному Автором плану квест продолжается после воды: мука из
запертого дома Мирона, ягоды из закутка за телегой, один ягодный пирог.
В доме два отдельных обычных сундука. Ягоды разрешено собирать заранее,
а передавать Агафье — после муки. Награды за промежуточные этапы нет.

## Ресурсы и размещение

- [Ягодный куст](../../core/src/main/assets/environment/custom_tiles/surface_berry_bush.png)
  — 64×32: два кадра 32×32, слева с ягодами, справа после сбора.
  Основание (47–48, 12) непроходимо и пропускает обзор; верхний ряд
  (47–48, 11) рисуется в `customWalls`. Оба слоя сохраняют состояние.
- [Мешок муки](../../core/src/main/assets/sprites/flour_sack.png) — 16×16,
  рисунок 12×14. Отдельный сундук в доме Мирона (31, 11).
- [Лесные ягоды](../../core/src/main/assets/sprites/pie_berries.png) — 16×16,
  рисунок 12×12. Один квестовый предмет за единственный сбор.
- [Ягодный пирог](../../core/src/main/assets/sprites/berry_pie.png) — готовый
  спрайт из предыдущего обсуждения. Подключён как `BerryPie extends Food`;
  подробности его подготовки — в [отдельном документе](berry-pie.md).
- В [атласе предметов](../../core/src/main/assets/sprites/items.png) заняты
  свободные слоты `FOOD+13` (445), `QUEST+14` (478), `QUEST+15` (479).
  Все остальные пиксели атласа сохранены.
- Дверь дома Мирона (32, 14) — `LOCKED_DOOR`; обычный железный ключ
  поверхности выдаётся один раз, после просьбы о муке. Рамы семи специальных
  дверных тайлов 58, 114, 216–219, 228 в `tiles_surface.png` теперь деревянные.
  Сохранены исходные серые замки, створки, металл, альфа-канал и остальные
  слоты. Доска осталась в отдельном незапертом сундуке (35, 12).

Исходные результаты встроенного `image_gen` сохранены как
`berry-bush-generated.png`, `flour-sack-generated.png`,
`pie-berries-generated.png`, `surface-locked-doors-generated.png`.
Для редактирования дверей использованы
[лист исходных тайлов](surface-locked-doors-reference.png) и атлас поверхности.
Готовые игровые ресурсы собраны
[скриптом подготовки](prepare-berry-quest.ps1): выборка ближайшего пикселя
без сглаживания, перенос в игровые атласы, выбор деревянного материала
из результата редактирования по маске исходной каменной рамы.
Непрозрачность новых спрайтов берётся из сгенерированных изображений.

Проверены [увеличенный вид новых спрайтов](berry-quest-preview.png),
[специальные дверные тайлы](surface-locked-doors-preview.png), размеры PNG,
соответствие кадров слотам, сохранность пикселей вне целевых слотов
и альфа-канала дверей. Игровое прохождение и автотесты не запускались.

`:core:compileJava` и `:android:assembleDebug` успешны. APK установлен
обновлением на планшет по Wi-Fi ADB с явным serial
`adb-eb278ce2-Gf1NFr._adb-tls-connect._tcp`, результат `Success`.
Подтверждено время обновления `2026-11-04 01:37:13`; все 14 новых
и изменённых графических и текстовых ресурсов в APK совпадают с исходниками
по SHA-256. Игра не запускалась. Для новой расстановки нужно новое прохождение,
если поверхность уже была создана.

## Промпты встроенного image_gen

### Куст: два состояния

```text
Use case: stylized-concept. Asset type: a two-frame environment sprite sheet for a Shattered Pixel Dungeon-based pixel RPG. Create ONE low round woodland berry bush in two matching states, side by side: LEFT with ripe dark red-purple berries, RIGHT exactly the same bush after its berries have been gathered, with no fruit remaining. Sheet is a 64x32 logical pixel grid, each frame 32x32; output as large exact nearest-neighbour blocks, genuinely transparent background. Bushes have the same modest compact silhouette, dark earthy outlines, muted olive-green leaves, woody brown twigs at bottom, limited palette and large pixel clusters like the attached game's vegetation reference; top-down three-quarter game view matching Pixel Dungeon trees. Each bush centered in its frame, occupies about 28x26 logical pixels, baseline at logical y=30, with 2 pixels empty margin on sides and top. Fruit should form a few clearly distinct red-purple 2-pixel clusters. The two states have identical alignment, silhouette, foliage and branch structure; change only fruit to leaves in the right frame. No grid lines, text, captions, frames, background, ground tiles, grass, cast shadows, gradients, antialiasing, loose fruit, extra plants or objects. Reference local sprite atlas is style guidance only.
```

### Мешок муки

```text
Use case: stylized-concept. Asset type: ONE tiny 16x16 logical-pixel inventory item sprite in the style of Shattered Pixel Dungeon. Primary request: a short plump beige linen sack of flour with its mouth folded loosely open, a small pale off-white patch of flour visible at the opening, tied dark ochre cord below the rim. Compact simple readable coarse pixel art, dark brown 1-pixel outline, three warm linen shades, off-white flour, flat pixel clusters, top-down three-quarter game view. Center it on a genuinely transparent square background with 2 logical pixels of empty margin all around; object about 12x13 logical pixels. Show the 16x16 grid as large perfectly solid nearest-neighbour square blocks, not fine details. No text, letters, label, wheat emblem, scenery, hands, plate, tools, extra sacks, shadow outside silhouette, gradients, blur or antialiasing. Palette and detail should match the game's tiny food and quest inventory icons.
```

### Собранные ягоды

```text
Use case: stylized-concept. Asset type: ONE tiny 16x16 logical-pixel inventory item sprite for Shattered Pixel Dungeon-style RPG. A small handful of ripe dark red and plum-purple woodland berries resting in a folded ochre-brown scrap of cloth, with just one small muted olive leaf. Very simple coarse pixel art, hard dark brown pixel outline, restrained palette of 7-10 flat colors, three-quarter top-down game view, about 12x11 logical pixels centered in a transparent 16x16 logical-pixel square. Each logical pixel is a large uniform nearest-neighbour square block. Berries should be distinguishable as 2-3 pixel red-purple clusters, not glossy realistic fruit. Truly transparent background. No text, labels, hands, scenery, ground, basket, bowl, stems above the margin, extra objects, gradients, antialiasing, blur, external shadow or fine details. Match the compact scale and dark outline of original Pixel Dungeon food and quest icons.
```

### Деревянная рама запертой двери

```text
Use case: precise-object-edit. Image 1 EDIT TARGET is a 4-column by 2-row sheet of 16x16 logical-pixel locked-door tiles enlarged 8 times. Image 2 STYLE REFERENCE is this game's surface tile atlas with dark brown timber walls and wooden door frames. Replace ONLY the pale gray stone jambs/frames surrounding these locked doors with squared dark wooden beams matching the surface atlas. Preserve every original gray iron padlock and metal door band pixel, the brown door leaves, all black interiors and transparency, each sprite's exact geometry and position, and all empty areas. There are three locked-door parts on the top row and four wall/door side overhang pieces on the bottom. The top-right tile is padding and must remain unchanged. Ground strips should match the surface's existing brown plank floor rather than gray paving. Do not recolor metal locks brown. Simple flat muted brown timber palette: #2b2119 #3d2d20 #503b28 #644a30 #785b3a #8e7049 #a28559 #b69a6d. Native pixel art, exactly the same 64x32 logical pixel sheet layout; enlarged solid pixel blocks; no antialiasing, gradients, blur, extra locks, new handles, labels, borders, scenery, or changes to the rest of the atlas. True transparent background; output only edited Image 1.
```
