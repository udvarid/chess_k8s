import { Injectable } from '@angular/core';
import { UserDto } from '../shared/dto/userDto.model';
import { Subject } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { ChallengeCreateDto } from '../shared/dto/challengeCreateDto.model';
import { ChallengeDto } from '../shared/dto/challengeDto.model';
import { ChallengeActionDto } from '../shared/dto/challengeActionDto.model';
import { environment } from 'src/environments/environment';
import { ToastrService } from 'ngx-toastr';


@Injectable({ providedIn: 'root' })

export class ChallengeService {

    private users: UserDto[] = [];
    private challenges: ChallengeDto[] = [];
    usersChanged = new Subject<UserDto[]>();
    userListRefreshed = new Subject<boolean>();
    challengeChanged = new Subject<ChallengeDto[]>();
    challengeListRefreshed = new Subject<boolean>();
    pre: string;

    constructor(private http: HttpClient, private router: Router, private toastrService: ToastrService) {
        this.pre = environment.apiUrl;
    }

    getUserDetail() {
        this.userListRefreshed.next(true);
        const header = new HttpHeaders({});
        this.http.get(this.pre + '/api/user/listOfFreeUsers', {headers: header,  withCredentials: true })
        .subscribe((response: UserDto[]) => {
            this.users = response;
            this.usersChanged.next(response);
        });
    }

    getChallengeDetail() {
        this.challengeListRefreshed.next(true);
        const header = new HttpHeaders({});
        this.http.get(this.pre + '/api/challenge/listForTheRequester', {headers: header,  withCredentials: true })
        .subscribe((response: ChallengeDto[]) => {
            this.challenges = response;
            this.challengeChanged.next(response);
        });
    }

    challengeUser(challengedDto: ChallengeCreateDto) {
        const header = new HttpHeaders({});
        this.http.post(this.pre + '/api/challenge/create', challengedDto, {headers: header,  withCredentials: true }).subscribe( ans => {
            this.toastrService.success('Challenge set');
            this.getUserDetail();
            this.getChallengeDetail();
        }
        );
    }

    handleChallenge(answer: ChallengeActionDto) {
        const header = new HttpHeaders({});
        this.http.post(this.pre + '/api/challenge/answer', answer, {headers: header,  withCredentials: true }).subscribe( ans => {
            this.toastrService.success('Challenge answered');
            this.getUserDetail();
            this.getChallengeDetail();
        }
        );
    }

}
