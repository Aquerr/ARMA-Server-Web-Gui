// @ts-check
const tseslint = require("typescript-eslint");
const angular = require("angular-eslint");
const stylistic = require("@stylistic/eslint-plugin");
const { defineConfig } = require("eslint/config");

module.exports = defineConfig([
  {
    files: ["**/*.ts"],
    extends: [
      ...tseslint.configs.recommendedTypeChecked,
      {
        languageOptions: {
          parserOptions: {
            projectService: true
          }
        }
      },
      ...tseslint.configs.stylisticTypeChecked,
      ...angular.configs.tsRecommended,
      stylistic.configs.customize({
        indent: 2,
        quotes: "double",
        semi: true,
        commaDangle: "never",
        arrowParens: true,
        braceStyle: "1tbs"
      })
    ],
    processor: angular.processInlineTemplates,
    rules: {
      "@angular-eslint/prefer-inject": ["off"],
      "@typescript-eslint/no-inferrable-types": ["off"],
      "@angular-eslint/prefer-standalone": ["warn"],
      "@typescript-eslint/unbound-method": [
        "error",
        {
          ignoreStatic: true
        }
      ],
      "@angular-eslint/directive-selector": [
        "error",
        {
          type: "attribute",
          prefix: "app",
          style: "camelCase"
        }
      ],
      "@angular-eslint/component-selector": [
        "error",
        {
          type: "element",
          prefix: "app",
          style: "kebab-case"
        }
      ]
    }
  },
  {
    files: ["**/*.html"],
    extends: [...angular.configs.templateRecommended, ...angular.configs.templateAccessibility],
    rules: {}
  }
]);
