import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MissionUploadButton } from './mission-upload-button';

describe('UploadMissionComponent', () => {
  let component: MissionUploadButton;
  let fixture: ComponentFixture<MissionUploadButton>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MissionUploadButton ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MissionUploadButton);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
