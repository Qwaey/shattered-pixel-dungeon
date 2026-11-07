# Вода, тропы и препятствия на поверхности

Дата обновления: 2026-11-07.

Для рек и затопленных участков используется сине-зелёная вода, для побережья — более холодная морская. На лугу кромку воды образует дёрн, у заболоченного берега — ил, у моря — галька. У мостков вода подходит прямо к доскам.

На суше прежние широкие дощатые дорожки стали вытоптанной землёй, гравием или серым грунтом у края света. Узкие земляные тропы Залесья и Лесной тропы сохраняют свою прежнюю форму. Выветренное дерево используется для мостов, пристаней и переходов над водой или разломами. Пол в зданиях использует прежний материал.

По краям лугов и дорог растут колючие заросли. В заболоченных местах их сменяют камыш и бурелом, в каменоломне — известняковые глыбы, на склонах — валуны и сланец, на побережье — коряги и груды обломков. Каждый объект имеет четыре варианта формы или тона. Земляные обрывы сохраняются на исходных картах, где они задают рельеф. Стены построек остаются архитектурой.

## Реализация

`SurfaceLandscapeTiles` выбирает материал по профилю секции. Берега, тропы и основания препятствий подбирают вариант по четырём соседним клеткам. Верхний слой рисует силуэт поверх основания. Туман и тени у таких клеток рисуются без нависания стены. `isDeckCell` задаёт настилы в тех секциях, где они предусмотрены планировкой.

Материалы выбираются при отрисовке. Исходные типы клеток, флаги проходимости и обзора, размещение противников, переходы и изменения карт по квестам остаются прежними.

| Ресурс | Содержимое |
| --- | --- |
| [tiles_campaign_surface.png](../../core/src/main/assets/environment/tiles_campaign_surface.png) | Атлас 256×784. Первые 576 слотов повторяют прежний атлас. Слоты 576–639 — берега, 640–687 — тропы, 688–703 — настилы, 704–751 — основания препятствий, 752–783 — силуэты. |
| [water_campaign_river.png](../../core/src/main/assets/environment/water_campaign_river.png) | Повторяемая текстура пресной воды 64×64. |
| [water_campaign_coast.png](../../core/src/main/assets/environment/water_campaign_coast.png) | Повторяемая текстура морской воды 64×64. |

## Создание ресурсов

Встроенный `image_gen` использован для двух прозрачных листов препятствий и квадратной основы воды. В качестве референсов использованы существующие трава и молодой дуб. Вырезки препятствий уменьшены точечной выборкой до размера клетки, варианты собраны отражением и небольшим изменением тона. Для воды выполнены точечная выборка, коррекция цвета и сведение противоположных краёв. Берега, грунт и настилы собраны пиксельными масками из существующей травы и деревянной фактуры.

<details>
<summary>Промпты image_gen</summary>

**Кусты, камыш и древесина**

Use case: stylized-concept. Asset: one transparent pixel art atlas source for a top-down 16 px tile RPG matching the supplied grass swatch and young oak. Generate exactly FOUR different impassable natural obstacles in one centered horizontal row of four equally sized columns, each object strictly inside its own quarter of the image, all on the same baseline, wide empty transparent gutters. Left to right (1) tangled low thorny dark olive hedge with 2 overlapping knotted bushes (2) dense river reed tuft with a few brown cattail heads and straw olive stems (3) weathered naturally fallen log with one short branch and broken end, facing diagonally NE-SW, long profile (4) high coastal driftwood pile with two silver-brown branches resting in a tangled heap. Ground-level soft compact shadows within the silhouette only. Orthographic top-down 3/4 game view, late 1990s handheld RPG pixel clusters, exactly one compact 16x16-style pixel sprite per item, hard aliased edges, clear simple volume, 5–8 muted colors per item, lit upper-left. These objects need to remain recognizable at 16 real pixels. No text, grid, labels, floor patches, ground texture, scenery, soft gradients or reflections. Real transparent background.

**Камень и обломки**

Use case: stylized-concept. Asset: transparent pixel art atlas source for a 16px top-down RPG matching the given muted Shattered Pixel Dungeon tiles. Generate exactly FOUR isolated terrain obstacles, horizontally in four equally sized columns, each strictly in its own quarter, all on one baseline, generous empty transparent gutters. Left to right (1) rounded grey-brown mossy boulder and two tiny adjacent stones (2) angular chalk-white limestone slab and quarry chips (3) pointed charcoal slate boulder with one split dark gray face and chips (4) broken masonry rubble heap of rough grey broken blocks, old structural debris without a vertical wall. Each is one distinct natural obstacle, compact top-down 3/4 overhead silhouette, screen-front low ground shadow under the object, upper-left light. 16x16 pixel sprite style, hard blocky pixels, 5–8 subdued colors per item, clear shapes and strong shaded volume even at 16px. No labels, grid, white background, ground patch, scenery, strokes or soft effects. Real transparent background.

**Вода**

Use case: stylized-concept. Asset: one seamless square tile of a naturally flowing outdoor river for a pixel-art RPG. View straight down, cropped to only water surface. Muted blue-green teal freshwater, dark cool base with tiny broken horizontal ripple strands and a few modest blue-green glints. Compact pixel clusters, intended to be sampled to a native 32x32 game tile. Small scale, low-contrast natural water texture that can repeat on all four sides, flat lighting, no circular rings, no islands, no rocks, no visible shore, no foam strips, no objects, no perspective, no bright cyan, no gradient vignette, no labels. Pixel art, quiet painterly limited colors consistent with the small dark olive grass tile supplied as palette context. Fill the entire square edge to edge with water; seamless tile.

</details>
