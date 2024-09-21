import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DifficultyComponent } from './difficulty.component';

describe('DifficultyComponent', () => {
  let component: DifficultyComponent;
  let fixture: ComponentFixture<DifficultyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DifficultyComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DifficultyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
