import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModUploadButtonComponent } from './mod-upload-button.component';

describe('UploadModComponent', () => {
  let component: ModUploadButtonComponent;
  let fixture: ComponentFixture<ModUploadButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModUploadButtonComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModUploadButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
