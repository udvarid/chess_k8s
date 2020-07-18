import { ChallengeAction } from '../enums/enums.model';

export interface ChallengeActionDto {
    challengeId: number;
    challengeAction: ChallengeAction;
}
