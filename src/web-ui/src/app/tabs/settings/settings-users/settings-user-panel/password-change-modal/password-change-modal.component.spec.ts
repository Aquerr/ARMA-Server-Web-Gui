import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PasswordChangeModalComponent } from './password-change-modal.component';

describe('PasswordChangeModalComponent', () => {
  let component: PasswordChangeModalComponent;
  let fixture: ComponentFixture<PasswordChangeModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PasswordChangeModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PasswordChangeModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
