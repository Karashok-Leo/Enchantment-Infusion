"""Static JEI wiring and layout regression checks; not a client-runtime test."""
import json
import math
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / 'src/main/java/karashokleo/enchantment_infusion'


class JeiIntegrationChecks(unittest.TestCase):
    def test_optional_entrypoint(self):
        metadata = json.loads((ROOT / 'src/main/resources/fabric.mod.json').read_text())
        self.assertEqual(metadata['entrypoints']['jei_mod_plugin'], [
            'karashokleo.enchantment_infusion.content.compat.jei.JEICompat'])
        self.assertNotIn('jei', metadata['depends'])
        for source in JAVA.rglob('*.java'):
            if 'compat/jei' not in source.as_posix():
                self.assertNotIn('mezz.jei', source.read_text(), str(source))

    def test_optional_development_runtime(self):
        build = (ROOT / 'build.gradle').read_text()
        self.assertIn("getOrElse('emi')", build)
        self.assertIn("['emi', 'jei', 'none', 'coexist', 'rei']", build)
        self.assertIn('modCompileOnly "mezz.jei:', build)
        self.assertIn("if (recipeViewer in ['jei', 'coexist'])", build)
        self.assertNotIn('modImplementation "mezz.jei:', build)
        self.assertIn('transitive = false', build)

    def test_recipe_registration_and_catalysts(self):
        plugin = (JAVA / 'content/compat/jei/JEICompat.java').read_text()
        self.assertIn('listAllOfType(EIRecipes.INFUSION_RECIPE_TYPE)', plugin)
        self.assertIn('new ItemStack(EIBlocks.INFUSION_TABLE), INFUSION', plugin)
        self.assertIn('new ItemStack(EIBlocks.INFUSION_PEDESTAL), INFUSION', plugin)
        for recipe in ['SimpleInfusionRecipe', 'EnchantmentInfusionRecipe']:
            self.assertIn('return EIRecipes.INFUSION_RECIPE_TYPE;',
                          (JAVA / f'content/recipe/{recipe}.java').read_text())

    def test_original_stacks_reach_jei(self):
        category = (JAVA / 'content/compat/jei/JEIInfusionCategory.java').read_text()
        self.assertIn('.addItemStacks(Arrays.asList(ingredient.getMatchingStacks()))', category)
        self.assertIn('.addItemStack(recipe.getOutput(world.getRegistryManager()))', category)
        self.assertNotIn('getDefaultStack', category)
        self.assertNotIn('registerSubtype', category)
        self.assertIn('recipe.getTableIngredient()', category)
        self.assertIn('recipe.getPedestalIngredient()', category)

    def test_one_to_eight_layouts(self):
        # Same centers/radius as EMI. Bounds include JEI's slot backgrounds.
        java_round = lambda x: math.floor(x + 0.5)
        for count in range(1, 9):
            with self.subTest(pedestals=count):
                boxes = [(31, 33, 49, 51), (107, 29, 133, 55)]
                for i in range(count):
                    radians = i / count * 2 * math.pi
                    x = java_round(40 - 29 * math.sin(radians))
                    y = java_round(42 - 29 * math.cos(radians))
                    boxes.append((x - 9, y - 9, x + 9, y + 9))
                for left, top, right, bottom in boxes:
                    self.assertTrue(0 <= left < right <= 138)
                    self.assertTrue(0 <= top < bottom <= 84)
                for i, a in enumerate(boxes):
                    for b in boxes[i + 1:]:
                        self.assertTrue(a[2] <= b[0] or b[2] <= a[0]
                                        or a[3] <= b[1] or b[3] <= a[1])


if __name__ == '__main__':
    unittest.main()
