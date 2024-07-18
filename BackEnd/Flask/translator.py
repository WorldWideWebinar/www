import ctranslate2
import transformers
# prevent libaray collision
import os
os.environ['KMP_DUPLICATE_LIB_OK'] = 'TRUE'

src_lang = "kor_Hang"
tgt_lang = "eng_Latn"

translator = ctranslate2.Translator("nllb-200-distilled-600M")
tokenizer = transformers.AutoTokenizer.from_pretrained("facebook/nllb-200-distilled-600M", src_lang=src_lang)
class Translator :
    def __init__(self, translator, tokenizer, tgt_lang) -> None:
        self.translator = translator
        self.tokenizer = tokenizer
        self.tgt_lang = tgt_lang
        pass
    def translate(self, text):
        source = self.tokenizer.convert_ids_to_tokens(self.tokenizer.encode(text))
        target_prefix = [self.tgt_lang]
        results = self.translator.translate_batch([source], target_prefix=[target_prefix])
        target = results[0].hypotheses[0][1:]
        return tokenizer.decode(self.tokenizer.convert_tokens_to_ids(target))
    


def translator_factory(tgt : str,src : str):
    tr = ctranslate2.Translator("nllb-200-distilled-600M", device = 'cuda')
    tk = transformers.AutoTokenizer.from_pretrained("facebook/nllb-200-distilled-600M", src_lang=src)
    tr = Translator(translator=tr,tokenizer=tk,tgt_lang=tgt)
    return tr




def translate(text):
    source = tokenizer.convert_ids_to_tokens(tokenizer.encode(text))
    target_prefix = [tgt_lang]
    results = translator.translate_batch([source], target_prefix=[target_prefix])
    target = results[0].hypotheses[0][1:]
    return tokenizer.decode(tokenizer.convert_tokens_to_ids(target))
