import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VoteCmdListItemComponent } from './vote-cmd-list-item.component';

describe('VoteCmdListItemComponent', () => {
  let component: VoteCmdListItemComponent;
  let fixture: ComponentFixture<VoteCmdListItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VoteCmdListItemComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(VoteCmdListItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
