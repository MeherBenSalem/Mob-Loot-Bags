"""Recipes, tags, advancements, models, blockstates, sounds.json and translations, per version profile.

Profiles (see loot.py): v3 = 26.3, v2 = 1.21.1 / 26.1.2 / 26.2, v1 = 1.20.1. `recipes_v` and `items_json` differ inside v2:
1.21.1 recipes use ingredient objects and has no items/ client definitions; 26.x use plain ingredient strings and items/.
"""
from __future__ import annotations

NS = 'mob_loot_bags'

BAG_IDS = {
    'common': 'commonlootbag', 'uncommon': 'uncommonlootbag', 'rare': 'rarelootbag', 'epic': 'epiclootbag',
    'legendary': 'legendarylootbag', 'mythic': 'mythiclootbag', 'cursed': 'cursed_lootbag', 'hourglass': 'timed_loot_bag',
    'locked': 'locked_lootbags', 'summoning': 'summoning_lootbags',
}
TIER_ORDER = ['common', 'uncommon', 'rare', 'epic', 'legendary', 'mythic']


class Profile:
    def __init__(self, loot, recipes, items_json, plural_dirs, advancement_icon_key, background):
        self.loot = loot                    # 'v1' | 'v2' | 'v3'
        self.recipes = recipes              # 'object' (1.20.1/1.21.1 ingredient objects) | 'string' (26.x)
        self.items_json = items_json        # True: write assets/<ns>/items/*.json (1.21.4+)
        self.plural_dirs = plural_dirs      # True for 1.20.1 (loot_tables/, recipes/, advancements/, tags/items/)
        self.icon_key = advancement_icon_key  # 'item' (1.20.1) or 'id'
        self.background = background        # advancement background id

    def dir(self, kind):
        if self.plural_dirs:
            return {'loot_table': 'loot_tables', 'recipe': 'recipes', 'advancement': 'advancements',
                    'tags/item': 'tags/items', 'tags/entity_type': 'tags/entity_types', 'tags/block': 'tags/blocks'}[kind]
        return kind


PROFILES = {
    '26.3': Profile('v3', 'string', True, False, 'id', f'{NS}:gui/advancements/backgrounds/loot_bags'),
    '26.2': Profile('v2', 'string', True, False, 'id', f'{NS}:gui/advancements/backgrounds/loot_bags'),
    '26.1.2': Profile('v2', 'string', True, False, 'id', f'{NS}:gui/advancements/backgrounds/loot_bags'),
    '1.21.1': Profile('v2', 'object', False, False, 'id', f'{NS}:textures/gui/advancements/backgrounds/loot_bags.png'),
    '1.20.1': Profile('v1', 'object', False, True, 'item', f'{NS}:textures/gui/advancements/backgrounds/loot_bags.png'),
}


# ----------------------------------------------------------------- recipes

def _ing(value, profile):
    if profile.recipes == 'string':
        return value
    return {'tag': value[1:]} if value.startswith('#') else {'item': value}


def _result(item, count, profile):
    if profile.loot == 'v1':
        out = {'item': item}
    else:
        out = {'id': item}
    if count != 1:
        out['count'] = count
    return out


def shaped(pattern, key, result, count, profile, category='misc'):
    return {'type': 'minecraft:crafting_shaped', 'category': category, 'pattern': pattern,
            'key': {k: _ing(v, profile) for k, v in key.items()}, 'result': _result(result, count, profile)}


def shapeless(ingredients, result, count, profile, category='misc'):
    return {'type': 'minecraft:crafting_shapeless', 'category': category,
            'ingredients': [_ing(i, profile) for i in ingredients], 'result': _result(result, count, profile)}


def recipes(profile):
    out = {}
    for i in range(1, 5):
        low, high = TIER_ORDER[i - 1], TIER_ORDER[i]
        out[f'{high}_loot_bag_recipe'] = shapeless([f'{NS}:{BAG_IDS[low]}'] * 4, f'{NS}:{BAG_IDS[high]}', 1, profile)
        out[f'{high}_loot_bag_downgrade_recipe'] = shapeless([f'{NS}:{BAG_IDS[high]}'], f'{NS}:{BAG_IDS[low]}', 4, profile)
    out['mythic_loot_bag_recipe'] = shapeless([f'{NS}:{BAG_IDS["legendary"]}'] * 4 + ['minecraft:nether_star'],
                                              f'{NS}:{BAG_IDS["mythic"]}', 1, profile)
    out['mythic_loot_bag_downgrade_recipe'] = shapeless([f'{NS}:{BAG_IDS["mythic"]}'], f'{NS}:{BAG_IDS["legendary"]}', 4, profile)
    out['bag_opener_recipe'] = shaped(['IHI', 'RER', 'ICI'], {
        'I': 'minecraft:iron_ingot', 'H': 'minecraft:hopper', 'R': 'minecraft:redstone', 'E': 'minecraft:emerald',
        'C': 'minecraft:chest'}, f'{NS}:loot_bag_opener_block', 1, profile, 'redstone')
    out['recycle_block_recipe'] = shaped(['IGI', 'SCS', 'IRI'], {
        'I': 'minecraft:iron_ingot', 'G': 'minecraft:grindstone', 'S': 'minecraft:smooth_stone', 'C': 'minecraft:composter',
        'R': 'minecraft:redstone'}, f'{NS}:loot_bag_recycle_block', 1, profile, 'redstone')
    out['fortune_talisman_recipe'] = shaped([' E ', 'GRG', ' G '], {
        'E': 'minecraft:emerald', 'G': 'minecraft:gold_ingot', 'R': 'minecraft:rabbit_foot'}, f'{NS}:fortune_talisman', 1, profile,
        'equipment')
    stonecut = {'type': 'minecraft:stonecutting', 'ingredient': _ing('minecraft:diamond', profile),
                'result': _result(f'{NS}:diamond_key', 1, profile)}
    if profile.loot == 'v1':
        stonecut = {'type': 'minecraft:stonecutting', 'ingredient': {'item': 'minecraft:diamond'},
                    'result': f'{NS}:diamond_key', 'count': 1}
    out['diamond_key_recipe'] = stonecut
    return out


# ----------------------------------------------------------------- tags

def tags(profile):
    bags = [f'{NS}:{i}' for i in BAG_IDS.values()]
    out = {
        ('tags/item', 'loot_bags'): {'values': bags},
        ('tags/item', 'recycler_junk'): {'values': [f'minecraft:{i}' for i in (
            'rotten_flesh', 'bone', 'string', 'arrow', 'feather', 'ink_sac', 'poisonous_potato', 'leather', 'wheat_seeds')]},
        ('tags/entity_type', 'bosses'): {'values': ['minecraft:wither', 'minecraft:ender_dragon', 'minecraft:warden',
                                                    'minecraft:elder_guardian', {'id': '#c:bosses', 'required': False}]},
        ('tags/entity_type', 'bag_carriers'): {'values': [f'minecraft:{m}' for m in (
            'zombie', 'husk', 'skeleton', 'stray', 'piglin', 'zombified_piglin', 'pillager', 'vindicator', 'wither_skeleton')]
            + ([{'id': 'minecraft:bogged', 'required': False}] if profile.loot != 'v1' else [])},
        ('tags/entity_type', 'no_bags'): {'values': ['minecraft:armor_stand']},
        ('tags/block', 'mineable/pickaxe'): None,  # written under the minecraft namespace, see build.py
    }
    for tier in TIER_ORDER:
        out[('tags/item', f'bonus/{tier}')] = {'values': []}
    return out


# ----------------------------------------------------------------- advancements

ADVANCEMENTS = [
    # id, parent, icon item, frame, criteria trigger, item (or #tag), hidden
    ('root', None, 'legendarylootbag', 'task', 'inventory_changed', '#mob_loot_bags:loot_bags', False),
    ('open_bag', 'root', 'commonlootbag', 'task', 'consume_item', '#mob_loot_bags:loot_bags', False),
    ('open_rare', 'open_bag', 'rarelootbag', 'task', 'consume_item', 'mob_loot_bags:rarelootbag', False),
    ('open_epic', 'open_rare', 'epiclootbag', 'goal', 'consume_item', 'mob_loot_bags:epiclootbag', False),
    ('open_legendary', 'open_epic', 'legendarylootbag', 'challenge', 'consume_item', 'mob_loot_bags:legendarylootbag', False),
    ('open_mythic', 'open_legendary', 'mythiclootbag', 'challenge', 'consume_item', 'mob_loot_bags:mythiclootbag', True),
    ('open_cursed', 'open_bag', 'cursed_lootbag', 'task', 'consume_item', 'mob_loot_bags:cursed_lootbag', False),
    ('open_hourglass', 'open_bag', 'timed_loot_bag', 'goal', 'consume_item', 'mob_loot_bags:timed_loot_bag', False),
    ('open_locked', 'open_bag', 'locked_lootbags', 'goal', 'consume_item', 'mob_loot_bags:locked_lootbags', False),
    ('open_summoning', 'open_bag', 'summoning_lootbags', 'task', 'consume_item', 'mob_loot_bags:summoning_lootbags', False),
    ('fortune_talisman', 'open_bag', 'fortune_talisman', 'task', 'inventory_changed', 'mob_loot_bags:fortune_talisman', False),
    ('bag_opener', 'root', 'loot_bag_opener_block', 'task', 'inventory_changed', 'mob_loot_bags:loot_bag_opener_block', False),
    ('loot_recycler', 'bag_opener', 'loot_bag_recycle_block', 'task', 'inventory_changed', 'mob_loot_bags:loot_bag_recycle_block', False),
]


def _predicate(item, profile):
    if profile.loot == 'v1':
        return {'tag': item[1:]} if item.startswith('#') else {'items': [item]}
    return {'items': item}


def advancements(profile):
    out = {}
    for adv_id, parent, icon, frame, trigger, item, hidden in ADVANCEMENTS:
        if trigger == 'consume_item':
            conditions = {'item': _predicate(item, profile)}
        else:
            conditions = {'items': [_predicate(item, profile)]}
        display = {
            'icon': {profile.icon_key: f'{NS}:{icon}'},
            'title': {'translate': f'advancements.{NS}.{adv_id}.title'},
            'description': {'translate': f'advancements.{NS}.{adv_id}.description'},
            'frame': frame,
            'show_toast': parent is not None,
            'announce_to_chat': parent is not None,
            'hidden': hidden,
        }
        if parent is None:
            display['background'] = profile.background
        adv = {'display': display, 'criteria': {'get': {'trigger': f'minecraft:{trigger}', 'conditions': conditions}},
               'requirements': [['get']]}
        if parent:
            adv['parent'] = f'{NS}:{parent}'
        if frame == 'challenge':
            adv['rewards'] = {'experience': 100 if adv_id == 'open_legendary' else 250}
        out[adv_id] = adv
    # Recipe unlocks: the machines and the talisman show up in the recipe book once you find a bag.
    for recipe in ('bag_opener_recipe', 'recycle_block_recipe', 'fortune_talisman_recipe', 'mythic_loot_bag_recipe'):
        out[f'recipes/misc/{recipe}'] = {
            'parent': 'minecraft:recipes/root',
            'criteria': {
                'has_bag': {'trigger': 'minecraft:inventory_changed', 'conditions': {'items': [_predicate('#mob_loot_bags:loot_bags', profile)]}},
                'has_the_recipe': {'trigger': 'minecraft:recipe_unlocked',
                                   'conditions': {('recipes' if profile.loot == 'v3' else 'recipe'): f'{NS}:{recipe}'}},
            },
            'requirements': [['has_bag', 'has_the_recipe']],
            'rewards': {'recipes': [f'{NS}:{recipe}']},
        }
    return out


# ----------------------------------------------------------------- client assets

def item_models():
    out = {}
    from items import FILES
    for key, item_id in BAG_IDS.items():
        out[item_id] = {'parent': 'minecraft:item/generated', 'textures': {'layer0': f'{NS}:item/{FILES[key]}'}}
    out['diamond_key'] = {'parent': 'minecraft:item/generated', 'textures': {'layer0': f'{NS}:item/diamond_key'}}
    out['fortune_talisman'] = {'parent': 'minecraft:item/generated', 'textures': {'layer0': f'{NS}:item/fortune_talisman'}}
    out['loot_bag_opener_block'] = {'parent': f'{NS}:block/bag_opener'}
    out['loot_bag_recycle_block'] = {'parent': f'{NS}:block/loot_recycler'}
    return out


def block_models():
    def orientable(front, prefix):
        return {'parent': 'minecraft:block/orientable_with_bottom', 'textures': {
            'top': f'{NS}:block/{prefix}_top', 'bottom': f'{NS}:block/{prefix}_bottom', 'side': f'{NS}:block/{prefix}_side',
            'front': f'{NS}:block/{front}', 'particle': f'{NS}:block/{prefix}_side'}}
    return {
        'bag_opener': orientable('bag_opener_front', 'bag_opener'),
        'bag_opener_on': orientable('bag_opener_front_on', 'bag_opener'),
        'loot_recycler': orientable('loot_recycler_front', 'loot_recycler'),
        'loot_recycler_on': orientable('loot_recycler_front_on', 'loot_recycler'),
    }


def blockstates():
    rot = {'north': 0, 'east': 90, 'south': 180, 'west': 270}

    def states(model):
        variants = {}
        for facing, y in rot.items():
            for active in ('false', 'true'):
                v = {'model': f'{NS}:block/{model}{"_on" if active == "true" else ""}'}
                if y:
                    v['y'] = y
                variants[f'active={active},facing={facing}'] = v
        return {'variants': variants}
    return {'loot_bag_opener_block': states('bag_opener'), 'loot_bag_recycle_block': states('loot_recycler')}


def item_definitions():
    """1.21.4+ client item definitions (assets/<ns>/items/<id>.json)."""
    return {item_id: {'model': {'type': 'minecraft:model', 'model': f'{NS}:item/{item_id}'}}
            for item_id in item_models()}


def sounds_json():
    def s(names, subtitle, stream=False):
        return {'subtitle': f'subtitles.{NS}.{subtitle}', 'sounds': [{'name': f'{NS}:{n}', 'stream': stream} for n in names]}
    return {
        'bag.open': s(['bag_open_1', 'bag_open_2', 'bag_open_3'], 'bag_open'),
        'bag.rustle': s(['bag_rustle_1', 'bag_rustle_2', 'bag_rustle_3'], 'bag_rustle'),
        'bag.drop': s(['bag_drop'], 'bag_drop'),
        'bag.reveal.rare': s(['reveal_rare'], 'reveal'),
        'bag.reveal.epic': s(['reveal_epic'], 'reveal'),
        'bag.reveal.legendary': s(['reveal_legendary'], 'reveal_legendary'),
        'bag.reveal.mythic': s(['reveal_mythic'], 'reveal_mythic'),
        'bag.cursed': s(['cursed'], 'cursed'),
        'bag.unlock': s(['unlock'], 'unlock'),
        'bag.hourglass_ripe': s(['hourglass_ripe'], 'hourglass_ripe'),
        'machine.opener': s(['machine_opener_1', 'machine_opener_2'], 'machine_opener'),
        'machine.recycler': s(['machine_recycler_1', 'machine_recycler_2'], 'machine_recycler'),
        'dramatic': s(['dramatic-sound-effect'], 'cursed', stream=True),
        'lootbag_sfx_1': s(['lootbag_1'], 'cursed'),
        'lootbag_sfx_2': s(['lootbag_2'], 'cursed'),
    }
