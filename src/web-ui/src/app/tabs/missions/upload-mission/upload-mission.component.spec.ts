import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadMissionComponent } from './upload-mission.component';

describe('UploadMissionComponent', () => {
  let component: UploadMissionComponent;
  let fixture: ComponentFixture<UploadMissionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UploadMissionComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UploadMissionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
