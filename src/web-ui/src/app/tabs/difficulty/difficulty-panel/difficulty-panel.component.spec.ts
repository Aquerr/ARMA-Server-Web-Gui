import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DifficultyPanelComponent } from './difficulty-panel.component';

describe('DifficultyPanelComponent', () => {
  let component: DifficultyPanelComponent;
  let fixture: ComponentFixture<DifficultyPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DifficultyPanelComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DifficultyPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
