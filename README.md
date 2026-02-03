# Fabric Example Mod

## Setup

For setup instructions please see the [fabric documentation page](https://docs.fabricmc.net/develop/getting-started/setting-up) that relates to the IDE that you are using.

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.

## File Tree
```
src/
├── main/
│   ├── java/com/example/
│   │   ├── CatMod.java                <-- Main entry (ModInitializer)
│   │   ├── entity/
│   │   │   └── CatArmorAccessor.java  <-- The Interface
│   │   ├── item/
│   │   │   └── CatArmorItem.java
│   │   ├── mixin/
│   │   │   ├── CatHealMixin.java
│   │   │   ├── CatArmorDataMixin.java
│   │   │   └── CatArmorInteractionMixin.java
│   │   └── registry/
│   │       └── ModItems.java
│   └── resources/
│       ├── fabric.mod.json
│       ├── mc_cat_mod.mixins.json
│       ├── assets/mc_cat_mod/...       <-- Textures/Models
│       └── data/mc_cat_mod/recipes/   <-- Recipe JSONs
└── client/
    ├── java/com/example/
    │   ├── CatModClient.java          <-- Client entry (ClientModInitializer)
    │   ├── client/render/
    │   │   ├── CatArmorFeatureRenderer.java
    │   │   └── CatArmorRenderStateAccessor.java
    │   └── mixin/client/
    │       ├── CatRendererMixin.java         <-- Client-only mixin (adds render layer + copies armor into render state)
    │       ├── CatRenderStateArmorMixin.java <-- Client-only mixin (stores armor ItemStack on CatRenderState)
    │       └── ExampleClientMixin.java       <-- Template mixin (safe to remove if unused)
    └── resources/
        └── mc_cat_mod.client.mixins.json     <-- Client mixin config (listed in fabric.mod.json)
```