import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DifficultyDeleteConfirmDialogComponent } from './difficulty-delete-confirm-dialog.component';

describe('DifficultyDeleteConfirmDialogComponent', () => {
  let component: DifficultyDeleteConfirmDialogComponent;
  let fixture: ComponentFixture<DifficultyDeleteConfirmDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DifficultyDeleteConfirmDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DifficultyDeleteConfirmDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
