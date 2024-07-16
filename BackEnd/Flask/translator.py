import ctranslate2
import transformers

src_lang = "kor_Hang"
tgt_lang = "eng_Latn"

translator = ctranslate2.Translator("nllb-200-distilled-600M")
tokenizer = transformers.AutoTokenizer.from_pretrained("facebook/nllb-200-distilled-600M", src_lang=src_lang)

def translate(text):
    source = tokenizer.convert_ids_to_tokens(tokenizer.encode(text))
    target_prefix = [tgt_lang]
    results = translator.translate_batch([source], target_prefix=[target_prefix])
    target = results[0].hypotheses[0][1:]
    return tokenizer.decode(tokenizer.convert_tokens_to_ids(target))
