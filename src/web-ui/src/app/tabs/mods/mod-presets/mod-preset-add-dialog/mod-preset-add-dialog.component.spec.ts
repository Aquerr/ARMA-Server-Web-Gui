import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModPresetAddDialogComponent } from './mod-preset-add-dialog.component';

describe('ModPresetAddDialogComponent', () => {
  let component: ModPresetAddDialogComponent;
  let fixture: ComponentFixture<ModPresetAddDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ModPresetAddDialogComponent]
    });
    fixture = TestBed.createComponent(ModPresetAddDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
