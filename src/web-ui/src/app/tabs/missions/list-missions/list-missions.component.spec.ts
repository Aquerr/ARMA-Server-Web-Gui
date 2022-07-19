import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListMissionsComponent } from './list-missions.component';

describe('ListMissionsComponent', () => {
  let component: ListMissionsComponent;
  let fixture: ComponentFixture<ListMissionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ListMissionsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListMissionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
