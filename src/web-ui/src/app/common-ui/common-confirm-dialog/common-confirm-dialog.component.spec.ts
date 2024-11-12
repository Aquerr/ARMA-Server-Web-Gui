import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommonConfirmDialogComponent } from './common-confirm-dialog.component';

describe('CommonConfirmDialogComponent', () => {
  let component: CommonConfirmDialogComponent;
  let fixture: ComponentFixture<CommonConfirmDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommonConfirmDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CommonConfirmDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
