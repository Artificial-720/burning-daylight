# Burning Daylight

**Burning Daylight** is a Minecraft plugin that introduces a new survival mechanic: players take damage when exposed to direct sunlight. Use the environment, time of day, and protective gear to survive.

## Downloads

- **[Bukkit/Paper](https://modrinth.com/plugin/burning-daylight)**

## Features

- **Sunburn Damage**: Direct exposure to sunlight causes damage—find shade or go underground to stay safe.
  - Damage duration and intensity are fully configurable.
- **Nighttime Safety**: Reduced damage at night. This is configurable through settings.
- **Weather Protection**: Rainy or stormy weather diminishes sunlight. This is configurable through settings.
- **Grace Period**:
  - Players start with a 5-minute grace period (default), during which they are immune to sunburn damage.
  - Grace periods can be applied on the first join or upon respawning, with customizable start/end messages.
- **Customizable Damage Prevention**:
  - Sunburn damage can be prevented in specific dimensions, such as the Nether or the End.
  - Fire Resistance potions can also grant immunity if enabled in the configuration.
- **Armor Protection**:
  - Leather armor is the default protective gear, reducing damage by 25% per piece (full leather armor negates 100% of damage).
  - Other armor types (iron, gold, diamond, netherite) can be configured for protection through the settings.
  - Fire Protection enchantments further reduce damage.
- **Daytime Durability Damage**:
  - Armor takes durability damage from sun exposure during the day (default).
  - Durability loss can also be toggled for nighttime and weather.
- **Exempt Players**: Specific players can be exempted from sunburn damage via configuration.

## Commands

- `/burn reload`: Reloads the plugin's configuration file.
- `/burn exempt <playerName>`: Exempts a player from receiving damage.
- `/burn unexempt <playerName>`: Unexempts a player from receiving damage.

## Permissions

- `burn.reload`: Grants access to the `/burn reload` command.
- `burn.exempt`: Grants access to the `/burn exempt` command.
- `burn.unexempt`: Grants access to the `/burn unexempt` command.

## Installation and Configuration

1. **Install**:
   - Download the plugin `.jar` file and place it in your server's `plugins` directory.
   - Restart the server to load the plugin.

2. **Configure**:
   - Open the `config.yml` file in your server's `plugins/BurningDaylight` directory. 
   - Edit the values as desired. 
   - Save the file and reload the plugin using `/burn reload`.

## Reporting Issues

For bug reports, suggestions, or general feedback, please visit the [GitHub Issues Page](https://github.com/Artificial-720/burning-daylight/issues).