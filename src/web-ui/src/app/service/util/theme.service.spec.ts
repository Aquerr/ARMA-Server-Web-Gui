import { DOCUMENT } from "@angular/common";
import { TestBed } from "@angular/core/testing";
import { beforeEach, describe, expect, it } from "vitest";

import { ThemeService } from "./theme.service";

describe("ThemeService", () => {
  let mockDocument: Document;

  function createService(): ThemeService {
    return TestBed.inject(ThemeService);
  }

  beforeEach(() => {
    localStorage.clear();

    mockDocument = document.implementation.createHTMLDocument("test");

    TestBed.configureTestingModule({
      providers: [{ provide: DOCUMENT, useValue: mockDocument }]
    });
  });

  describe("initialization", () => {
    it("defaults to 'dark' when nothing is stored", () => {
      const service = createService();

      expect(service.theme()).toBe("dark");
      expect(service.darkMode()).toBe(true);
    });

    it("restores 'light' from localStorage", () => {
      localStorage.setItem("theme", "light");

      const service = createService();

      expect(service.theme()).toBe("light");
      expect(service.darkMode()).toBe(false);
    });

    it("restores 'dark' from localStorage", () => {
      localStorage.setItem("theme", "dark");

      const service = createService();

      expect(service.theme()).toBe("dark");
      expect(service.darkMode()).toBe(true);
    });

    it("falls back to 'dark' for an invalid stored value", () => {
      localStorage.setItem("theme", "blue");

      const service = createService();

      expect(service.theme()).toBe("dark");
    });

    it("applies the resolved theme to the document element on construction", () => {
      localStorage.setItem("theme", "light");

      createService();

      expect(mockDocument.documentElement.dataset["theme"]).toBe("light");
    });
  });

  describe("setTheme", () => {
    it("updates the theme signal", () => {
      const service = createService();

      service.setTheme("light");

      expect(service.theme()).toBe("light");
    });

    it("persists the theme to localStorage", () => {
      const service = createService();

      service.setTheme("light");

      expect(localStorage.getItem("theme")).toBe("light");
    });

    it("applies the theme to the document element", () => {
      const service = createService();

      service.setTheme("light");
      expect(mockDocument.documentElement.dataset["theme"]).toBe("light");

      service.setTheme("dark");
      expect(mockDocument.documentElement.dataset["theme"]).toBe("dark");
    });

    it("updates the darkMode computed signal", () => {
      const service = createService();

      service.setTheme("light");
      expect(service.darkMode()).toBe(false);

      service.setTheme("dark");
      expect(service.darkMode()).toBe(true);
    });
  });

  describe("changeTheme", () => {
    it("switches from dark to light", () => {
      const service = createService();
      service.setTheme("dark");

      service.changeTheme();

      expect(service.theme()).toBe("light");
    });

    it("switches from light to dark", () => {
      const service = createService();
      service.setTheme("light");

      service.changeTheme();

      expect(service.theme()).toBe("dark");
    });

    it("persists the toggled theme to localStorage", () => {
      const service = createService();
      service.setTheme("dark");

      service.changeTheme();

      expect(localStorage.getItem("theme")).toBe("light");
    });
  });

  describe("isDarkMode", () => {
    it("returns true when the theme is dark", () => {
      const service = createService();
      service.setTheme("dark");

      expect(service.isDarkMode()).toBe(true);
    });

    it("returns false when the theme is light", () => {
      const service = createService();
      service.setTheme("light");

      expect(service.isDarkMode()).toBe(false);
    });

    it("stays in sync after changeTheme is called", () => {
      const service = createService();
      service.setTheme("light");

      service.changeTheme();

      expect(service.isDarkMode()).toBe(true);
    });
  });
});
