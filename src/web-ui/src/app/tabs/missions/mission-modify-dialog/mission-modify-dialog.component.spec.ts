import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MissionModifyDialogComponent } from './mission-modify-dialog.component';

describe('MissionModifyDialogComponent', () => {
  let component: MissionModifyDialogComponent;
  let fixture: ComponentFixture<MissionModifyDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MissionModifyDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MissionModifyDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
