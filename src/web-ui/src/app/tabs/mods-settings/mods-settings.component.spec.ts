import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModsSettingsComponent } from './mods-settings.component';

describe('ModsSettingsComponent', () => {
  let component: ModsSettingsComponent;
  let fixture: ComponentFixture<ModsSettingsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModsSettingsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ModsSettingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
