# Спрайты квестовых сцен

Дата обновления 2026-11-08.

Каждый новый атлас состоит из 16 прозрачных кадров 16×16. Нумерация идёт
слева направо и сверху вниз. Кадры объектов перечислены в `Fixture`, кадры
жителей сопоставлены со сценами в `CampaignPersonSprite`. Несколько встреч
одного жителя могут использовать общий кадр.

- [Механизмы и следы](../../core/src/main/assets/sprites/campaign_fixtures.png).
- [Жители](../../core/src/main/assets/sprites/campaign_villagers.png).

Оба исходных листа созданы встроенным imagegen. Художник по текстурам
и спрайтам вырезал фигуры по альфа-каналу, подогнал их точечной выборкой
к размеру игрового кадра и проверил на траве, земле и настиле в масштабах
1× и 3×. Нижние непрозрачные пиксели стоящих объектов и жителей
приходятся на строку 14 при отсчёте от нуля.

<details>
<summary>Промпт объектов</summary>

Референс стиля — `sprites/items.png`.

> Use case: stylized-concept. Generate a single transparent pixel-art game sprite sheet for an overhead medieval fantasy RPG, using the supplied Shattered Pixel Dungeon inventory atlas only as style reference. Exactly 4 columns by 4 rows in a square 1024x1024 canvas, each cell 256x256, no drawn grid. Each cell has exactly one isolated silhouette centered in its own cell with wide transparent margins on all sides. Read left to right, top to bottom: row 1 (1) dark oak signpost with a right-pointing signboard bearing three pale carved slashes, (2) closed low wooden wicket gate in a short frame with dark iron latch, (3) antique iron hand water pump with curved handle and short spout, (4) short robust iron switch lever angled on a square bolted plinth. Row 2 (5) old bronze bell hanging in a small timber frame, (6) thick coiled hemp rope with a hook, (7) lit amber glass lantern in dark metal frame, (8) flat rough dark gray carved stone slab bearing two pale handprints. Row 3 (9) pair of muddy footprints on transparent ground, no base, (10) open wood crate showing two planks across top, (11) round glass pale blue beacon lens in bronze frame, (12) timber wheel winch with coil and iron crank. Row 4 (13) low short two-step weathered wood stair platform with an iron bracket, (14) iron mooring anchor with a short bit of chain, (15) pale triangular folded sail cloth with a visible patched seam, (16) little hexagonal wooden bucket seat suspended by two angled ropes, a one-person ferry cradle. Purpose: displayed as world props at native 16 by 16 pixels each. Use chunky bold colored pixel clusters, basic silhouettes, 4-7 colors per object, no micro detail, muted natural charcoal edges, light from upper left, top-down three-quarter RPG perspective, real transparency, no text, no gradients, no floor, no ground patches or cast shadows outside silhouette. Make each cell fully separate and recognizable when downsampled.

</details>

<details>
<summary>Промпт жителей</summary>

Референс стиля — `sprites/campaign_people.png`.

> Use case: stylized-concept. Create ONE 4×4 atlas source of full-body villagers for a native 16×16 pixel overhead medieval fantasy RPG. The supplied campaign_people.png is the style and scale reference only: compact body, large 6–8 px head, one dark pixel per eye, dark charcoal outline, short legs. Exact 4 rows and 4 columns, one centered figure per cell, square transparent canvas, broad completely empty gutters, same grounded standing stance and front three-quarter top-down view facing downscreen. All 16 figures are wholly isolated, no shadow or extra objects. Row 1 from left: middle-aged ferry bell keeper in russet vest and short brown hair; stocky landing keeper in blue-gray vest and sailor cap; farmhouse woman in russet dress and straw headscarf; gray-haired woman potter in ochre apron. Row 2: mature Vera in dark teal shawl and hair with a pale streak; shelter keeper woman in tan cloak and maroon hood; sturdy blond Domna in dark blue work dress; elderly flood witness in burgundy coat and gray beard. Row 3: orchard gardener man in moss apron and tan cap; a distinctly shorter brown-haired orchard girl with pigtails and pale blue dress; householder in lilac scarf and green dress; bell yard keeper man in charcoal shirt and red scarf. Row 4: shipyard foreman with brown leather apron and black beard; old signal keeper in faded navy and gold scarf; woman mason in chalk gray apron with dark hair wrapped in cloth; terrace gardener woman in moss-green shawl and brown dress. Style: actual coarse enlarged 16×16 pixel art, not high resolution pixel painting, visible large square pixel blocks, compact face, at most 7 colors per figure, muted earth tones. All adults share scale; the girl is shorter. No text, no grid, no backdrop, no shadows, no props, alpha-transparent.

</details>
