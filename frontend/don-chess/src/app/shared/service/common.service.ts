import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';


@Injectable({ providedIn: 'root' })
export class CommonService {

    public httpAnswer: Subject<boolean> = new Subject<boolean>();

    public signNewAnswer() {
        this.httpAnswer.next(true);
    }

}
