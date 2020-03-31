package com.thinktag.user;

import com.google.crypto.tink.proto.EcdsaParams;
import com.google.crypto.tink.proto.EcdsaSignatureEncoding;
import com.google.crypto.tink.proto.EllipticCurveType;
import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.RsaSsaPkcs1Params;
import com.google.crypto.tink.proto.RsaSsaPssParams;
import com.google.crypto.tink.subtle.EllipticCurves;

import java.security.GeneralSecurityException;

final class SigUtil {
    static final String INVALID_PARAMS = "Invalid ECDSA parameters";

    SigUtil() {
    }

    public static void validateEcdsaParams(EcdsaParams params) throws GeneralSecurityException {
        EcdsaSignatureEncoding encoding = params.getEncoding();
        HashType hash = params.getHashType();
        EllipticCurveType curve = params.getCurve();
        switch(encoding) {
            case DER:
            case IEEE_P1363:
                switch(curve) {
                    case NIST_P256:
                        if (hash != HashType.SHA256) {
                            throw new GeneralSecurityException("Invalid ECDSA parameters");
                        }
                        break;
                    case NIST_P384:
                        if (hash != HashType.SHA384 && hash != HashType.SHA512) {
                            throw new GeneralSecurityException("Invalid ECDSA parameters");
                        }
                        break;
                    case NIST_P521:
                        if (hash != HashType.SHA512) {
                            throw new GeneralSecurityException("Invalid ECDSA parameters");
                        }
                        break;
                    default:
                        throw new GeneralSecurityException("Invalid ECDSA parameters");
                }

                return;
            default:
                throw new GeneralSecurityException("unsupported signature encoding");
        }
    }

    public static void validateRsaSsaPkcs1Params(RsaSsaPkcs1Params params) throws GeneralSecurityException {
        toHashType(params.getHashType());
    }

    public static void validateRsaSsaPssParams(RsaSsaPssParams params) throws GeneralSecurityException {
        toHashType(params.getSigHash());
        if (params.getSigHash() != params.getMgf1Hash()) {
            throw new GeneralSecurityException("MGF1 hash is different from signature hash");
        }
    }

    public static com.google.crypto.tink.subtle.Enums.HashType toHashType(HashType hash) throws GeneralSecurityException {
        switch(hash) {
            case SHA256:
                return com.google.crypto.tink.subtle.Enums.HashType.SHA256;
            case SHA384:
                return com.google.crypto.tink.subtle.Enums.HashType.SHA384;
            case SHA512:
                return com.google.crypto.tink.subtle.Enums.HashType.SHA512;
            default:
                throw new GeneralSecurityException("unsupported hash type: " + hash.name());
        }
    }

    public static EllipticCurves.CurveType toCurveType(EllipticCurveType type) throws GeneralSecurityException {
        switch(type) {
            case NIST_P256:
                return EllipticCurves.CurveType.NIST_P256;
            case NIST_P384:
                return EllipticCurves.CurveType.NIST_P384;
            case NIST_P521:
                return EllipticCurves.CurveType.NIST_P521;
            default:
                throw new GeneralSecurityException("unknown curve type: " + type);
        }
    }

    public static EllipticCurves.EcdsaEncoding toEcdsaEncoding(EcdsaSignatureEncoding encoding) throws GeneralSecurityException {
        switch(encoding) {
            case DER:
                return EllipticCurves.EcdsaEncoding.DER;
            case IEEE_P1363:
                return EllipticCurves.EcdsaEncoding.IEEE_P1363;
            default:
                throw new GeneralSecurityException("unknown ECDSA encoding: " + encoding);
        }
    }
}
