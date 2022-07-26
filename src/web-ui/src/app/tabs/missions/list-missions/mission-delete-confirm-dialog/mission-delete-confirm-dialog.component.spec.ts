import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MissionDeleteConfirmDialogComponent } from './mission-delete-confirm-dialog.component';

describe('MissionDeleteConfirmDialogComponent', () => {
  let component: MissionDeleteConfirmDialogComponent;
  let fixture: ComponentFixture<MissionDeleteConfirmDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MissionDeleteConfirmDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MissionDeleteConfirmDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
