# Квест «Вода для приготовления еды»: текстуры

Дата обновления: 2026-11-01.

Ресурсы созданы встроенным инструментом `image_gen`, без CLI/API.
Референсы — настоящие спрайты героев и предметов Shattered Pixel Dungeon.
Три отдельных вызова: бабушка, пустое ведро, вода в том же ведре.

- [Бабушка](../../core/src/main/assets/sprites/grandmother.png): PNG RGBA 16×16, один неподвижный кадр.
- [Пустое ведро](../../core/src/main/assets/sprites/bucket.png): PNG RGBA 16×16.
- [Ведро с водой](../../core/src/main/assets/sprites/bucket_water.png): PNG RGBA 16×16.
- [Сравнение в увеличении](water-quest-preview.png): бабушка, существующий Дровосек, пустое и полное ведро. Увеличение без сглаживания.
- [Референс героев](water-quest-hero-reference.png): существующая подборка шести классов из `lumberjack-hero-references.png`, увеличенная без сглаживания до 1008×456.
- [Референс для наполнения](bucket-reference.png): итоговое пустое ведро, увеличенное в 32 раза без сглаживания.

## Подготовка игровых PNG

Генератор вернул изображения 1254×1254 с прозрачностью. Бабушка вырезана
по границе альфа-канала >128: (309, 197, 948, 1162), уменьшена до 10×15
выборкой ближайших пикселей и помещена в кадр 16×16 в (3, 0).
Пропорции соответствуют героям: широкая крупная голова, компактное тело,
короткие ноги. Бабушка седая, в платье и светлом переднике.

Пустое ведро вырезано по (328, 238, 926, 1027), уменьшено до 11×14
выборкой ближайших пикселей и помещено в (2, 1) кадра 16×16.
При подготовке полного ведра использована только сгенерированная синяя вода:
область (392, 338, 785, 423) результата третьего вызова уменьшена до 7×2.
Она перенесена в исходное пустое ведро в строку 6, колонки 4–10, и
строку 7, колонки 5–9 (координаты от нуля). Остальные цвета, деревянный
обод, ручка, обручи и весь альфа-канал сохранены.
Это исключает изменение формы ведра при наполнении.

В игровом атласе `sprites/items.png` использованы свободные слоты
`QUEST+11` и `QUEST+12`, индексы 475 и 476: начало (176, 464) и
(192, 464). В каждый перенесена область (2, 1, 13, 15) соответствующего
отдельного PNG, размер кадра в `ItemSpriteSheet` — 11×14.
Остальная часть атласа не менялась.

У бабушки один кадр в `GrandmotherSprite`, поэтому при ожидании не меняются
силуэт и поза. Предмет `Bucket` хранит воду в поле `filled`; смена состояния
обновляет значок в инвентаре и быстром слоте, имя и описание.

## Промпт бабушки

```text
Use case: stylized-concept. Asset type: production low resolution NPC sprite for Shattered Pixel Dungeon. Input image is STYLE REFERENCE ONLY: actual six hero classes enlarged nearest neighbour. Create ONE friendly elderly grandmother, standing still, facing slightly toward viewer's right in same three-quarter view as heroes. Very broad oversized head, grey hair in a small bun, kindly elderly face, muted plum/brown long dress with cream apron, short legs and small brown shoes. Exact same tiny pixel grammar and proportions as reference heroes: 12x15 native pixel silhouette placed in a 16x16 transparent cell, broad head in upper half rows 0-7, very compact body rows 8-12, short feet at row 14. Output a single 16x16 logical pixel sprite enlarged as square uniform blocks to 1024x1024, full canvas corresponds exactly to 16 by 16 logical pixels. Each logical pixel is one flat solid colour with no antialiasing or internal detail. Native outline width 1 pixel, muted game palette, about 12 colours. Character occupies at most 12 columns and 15 rows centered with transparent margins, bottom row transparent. No walking pose, no tool, no text, no grid, no ground, no shadow, no scenery. GENUINE TRANSPARENT BACKGROUND. Must resemble an actual additional original game NPC, not modern chibi illustration.
```

## Промпт пустого ведра

```text
Use case: stylized-concept. Asset type: production inventory icon in Shattered Pixel Dungeon. Input is STYLE REFERENCE ONLY actual game item atlas. Make ONE empty medieval wooden bucket with a curved dark iron handle, dark empty elliptical interior, warm brown staves and two grey iron bands. Simple three-quarter front view, matching tiny original game's item icon style. Exactly 16x16 logical pixels for the full square canvas, output enlarged as 64x uniform solid square blocks to 1024x1024. Bucket about 12 pixels wide by 14 pixels high, centered on transparent canvas with margin. Muted brown and grey palette, dark brown one-pixel contour, clear recognisable shape at native 16x16. No water. Every logical pixel one flat colour, no antialiasing, no texture within pixel blocks, no smooth gradients, no realism, no lettering, grid, ground or cast shadow. Genuine transparent background.
```

## Промпт ведра с водой

```text
Use case: precise-object-edit. Edit target is supplied production 16x16 logical pixel bucket, enlarged 32x nearest-neighbour to 512x512. Fill this SAME bucket with clean blue well water for the second inventory state. Keep every pixel of wooden staves, iron bands, handle, outline, silhouette, position, frame, size, palette of exterior and transparent background EXACTLY unchanged. Change ONLY the dark empty interior opening around logical columns 4-10 and rows 6-7 into a readable blue water surface with a few lighter cyan pixels. The opening is the area INSIDE the wooden rim directly below the handle; the empty arch above remains transparent. Water has 2 or 3 flat muted blue tones, no splash, no droplets. Preserve exact 16 by 16 logical pixel grid, render each pixel as one uniform square. No extra detail, no text, no grid, no ground, no shadow. Genuine transparent background. Same single sprite framing as input.
```

## Проверка

Просмотрены игровые PNG в увеличении рядом с существующим Дровосеком.
Проверены размеры и прозрачность, совпадение силуэта двух состояний ведра,
соответствие слотов атласа отдельным PNG и сохранность остальных пикселей
атласа. Компиляция и сведения об Android-сборке указаны в контексте проекта.
Игра и автотесты в рамках задачи не запускались.
