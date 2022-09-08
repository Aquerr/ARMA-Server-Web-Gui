import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadModComponent } from './upload-mod.component';

describe('UploadModComponent', () => {
  let component: UploadModComponent;
  let fixture: ComponentFixture<UploadModComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UploadModComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UploadModComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
