# 1.5.0.001

- Minecraft 1.20.1 port (Fabric + Forge 47)
- Runs on Spell Engine 1.10.5, Spell Power 1.6.0, RangedWeaponAPI 2.3.4 and ShieldAPI 2.2.0
- Item config: attribute modifier operations are now named `ADDITION` / `MULTIPLY_BASE` / `MULTIPLY_TOTAL`
  (they were `ADD_VALUE` / `ADD_MULTIPLIED_BASE` / `ADD_MULTIPLIED_TOTAL`) — existing
  `config/arsenal/{equipment_v2,shields,effects}.json` files need those three names replaced
- The `stun` effect no longer zeroes jump strength (no such attribute on 1.20.1); the stun itself is unchanged
- `absorption` grants and revokes its absorption points directly (no max-absorption attribute on 1.20.1)

# 1.5.0

- Adopt Spell Engine 1.10 changes
- NeoForge version no longer depends on Forgified Fabric API
- Fully translated content, now supporting 20 languages

# 1.4.3

- Update to latest version of Spell Engine

# 1.4.2

- Fixed some spell trigger placeholders

# 1.4.1

- Some melee weapons now come with spell choices
- Remove some deprecated API calls

# 1.4.0

DISCLAIMER: All spell books and spell scrolls will be reset, due to major API changes. Some (looted) weapons with custom spell containers become non-functional, and need to be re-obtained. Apologies for the inconvenience.

- Update to use Spell Engine 1.4.0

# 1.3.4

- Update translations
- Update some of the melee and ranged spell triggers

# 1.3.3

- Update spell power bonus of staves

# 1.3.2

- Fix some repair materials

# 1.3.1

- Add loot theme tags
- Update Bonus Shot (technical change)
- Update translations
- Fix scaling of Poison Cloud
- Make Dragonscale-Encrusted Longblade happier :)

# 1.3.0

- Migrate to Architectury

# 1.2.1

- Fix longblade damage
- Update to support latest Spell Engine 

# 1.2.0

Update to support latest Spell Engine

# 1.1.2

Fix ranged weapons always using default configuration.

# 1.1.1

Fix some emissive textures.

# 1.1.0

Add new weapons
- Great Hammer: Blackhand
- Mace: Stormherald
- Shield: Bastion of Light
- Sword: Dragonscale-Encrusted Longblade

# 1.0.5

- Rampaging, Focusing, Surging now have longer duration
- Update to latest Spell Engine

# 1.0.4

- Fix Surging passive spell not stacking
- Update translations

# 1.0.3

- Fix formatting error of Cooldown Touch spell description

# 1.0.2

- Fix some repair materials
- Update translations

# 1.0.1

Fix some of the passive spells triggering themselves

# 1.0.0

Initial release

# 