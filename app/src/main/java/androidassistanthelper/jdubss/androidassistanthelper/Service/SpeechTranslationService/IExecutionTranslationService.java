package androidassistanthelper.jdubss.androidassistanthelper.Service.SpeechTranslationService;

import ai.api.model.Result;

/**
 * Interface for executing an Intent from
 * api.ai DialogFlow response
 */
public interface IExecutionTranslationService {

    public Boolean translateAndExecute(Result result, IExecutionTranslationCallback executionTranslationCallback);

}
