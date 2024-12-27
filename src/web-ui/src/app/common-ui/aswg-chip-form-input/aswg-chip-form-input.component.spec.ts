import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AswgChipFormInputComponent } from './aswg-chip-form-input.component';

describe('AswgChipFormInputComponent', () => {
  let component: AswgChipFormInputComponent;
  let fixture: ComponentFixture<AswgChipFormInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AswgChipFormInputComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AswgChipFormInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
