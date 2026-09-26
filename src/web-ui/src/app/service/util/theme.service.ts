import { DOCUMENT } from "@angular/common";
import { computed, inject, Injectable, signal } from "@angular/core";

export type Theme = "light" | "dark";

const THEME_STORAGE_KEY = "theme";
const DEFAULT_THEME: Theme = "dark";

@Injectable({
  providedIn: "root"
})
export class ThemeService {
  private readonly document = inject(DOCUMENT);

  private readonly themeSignal = signal<Theme>(DEFAULT_THEME);

  readonly theme = this.themeSignal.asReadonly();
  readonly darkMode = computed(() => this.themeSignal() === "dark");

  constructor() {
    const theme = this.loadTheme();

    this.themeSignal.set(theme);
    this.applyTheme(theme);
  }

  setTheme(theme: Theme): void {
    this.themeSignal.set(theme);
    localStorage.setItem(THEME_STORAGE_KEY, theme);
    this.applyTheme(theme);
  }

  changeTheme(): void {
    this.setTheme(this.darkMode() ? "light" : "dark");
  }

  isDarkMode(): boolean {
    return this.darkMode();
  }

  private loadTheme(): Theme {
    const storedTheme = localStorage.getItem(THEME_STORAGE_KEY);

    return storedTheme === "light" || storedTheme === "dark"
      ? storedTheme
      : DEFAULT_THEME;
  }

  private applyTheme(theme: Theme): void {
    this.document.documentElement.dataset["theme"] = theme;
  }
}
