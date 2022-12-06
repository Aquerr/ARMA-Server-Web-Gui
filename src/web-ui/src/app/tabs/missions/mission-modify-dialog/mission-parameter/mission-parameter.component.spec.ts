import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MissionParameterComponent } from './mission-parameter.component';

describe('MissionParameterComponent', () => {
  let component: MissionParameterComponent;
  let fixture: ComponentFixture<MissionParameterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MissionParameterComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MissionParameterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
