import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MissionUploadButtonComponent } from './mission-upload-button.component';

describe('UploadMissionComponent', () => {
  let component: MissionUploadButtonComponent;
  let fixture: ComponentFixture<MissionUploadButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MissionUploadButtonComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MissionUploadButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
