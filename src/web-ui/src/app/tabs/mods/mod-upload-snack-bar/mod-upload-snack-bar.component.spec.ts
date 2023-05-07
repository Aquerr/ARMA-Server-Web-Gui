import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModUploadSnackBarComponent } from './mod-upload-snack-bar.component';

describe('ModUploadSnackBarComponent', () => {
  let component: ModUploadSnackBarComponent;
  let fixture: ComponentFixture<ModUploadSnackBarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModUploadSnackBarComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModUploadSnackBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
