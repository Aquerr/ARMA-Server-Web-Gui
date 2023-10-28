import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModPresetsComponent } from './mod-presets.component';

describe('ModPresetsComponent', () => {
  let component: ModPresetsComponent;
  let fixture: ComponentFixture<ModPresetsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ModPresetsComponent]
    });
    fixture = TestBed.createComponent(ModPresetsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
