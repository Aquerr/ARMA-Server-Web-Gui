import { CdkDragDrop, moveItemInArray, transferArrayItem } from "@angular/cdk/drag-drop";
import { Component, Input, OnDestroy, OnInit } from "@angular/core";

@Component({
    selector: 'app-list-mods',
    templateUrl: './list-mods.component.html',
    styleUrls: ['./list-mods.component.css']
})
export class ListModsComponent implements OnInit, OnDestroy {

    @Input() mods: string[] = [];

    @Input() header: string = '';

    constructor(){}

    ngOnDestroy(): void {
        
    }

    ngOnInit(): void {

    }

    drop(event: CdkDragDrop<string[]>){
        if (event.previousContainer === event.container){
            moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
        } else {
            transferArrayItem(
                event.previousContainer.data,
                event.container.data,
                event.previousIndex,
                event.currentIndex,
            );
        }
        this.mods.sort();
    }
}